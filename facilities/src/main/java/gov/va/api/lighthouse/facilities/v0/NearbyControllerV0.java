package gov.va.api.lighthouse.facilities.v0;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static gov.va.api.lighthouse.facilities.Controllers.validateServices;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.logging.log4j.util.Strings.isBlank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.ApiExceptions;
import gov.va.api.lighthouse.facilities.DriveTimeBandEntity;
import gov.va.api.lighthouse.facilities.DriveTimeBandRepository;
import gov.va.api.lighthouse.facilities.FacilityEntity;
import gov.va.api.lighthouse.facilities.FacilityRepository;
import gov.va.api.lighthouse.facilities.api.pssg.PathEncoder;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.NearbyResponse;
import gov.va.api.lighthouse.facilities.collector.InsecureRestTemplateProvider;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Validated
@RestController
@RequestMapping(value = "/v0/nearby")
@Slf4j
public class NearbyControllerV0 {
  private static final Set<Integer> DRIVE_TIME_VALUES = Set.of(10, 20, 30, 40, 50, 60, 70, 80, 90);

  private final FacilityRepository facilityRepository;

  private final DriveTimeBandRepository driveTimeBandRepository;

  private final RestTemplate restTemplate;

  private final String bingKey;

  private final String bingUrl;

  private final DeprecatedPssgDriveTimeBandSupport deprecatedPssgDriveTimeBandSupport =
      new DeprecatedPssgDriveTimeBandSupport();

  @Builder
  NearbyControllerV0(
      @Autowired FacilityRepository facilityRepository,
      @Autowired DriveTimeBandRepository driveTimeBandRepository,
      @Autowired InsecureRestTemplateProvider restTemplateProvider,
      @Value("${bing.key}") String bingKey,
      @Value("${bing.url}") String bingUrl) {
    this.facilityRepository = facilityRepository;
    this.driveTimeBandRepository = driveTimeBandRepository;
    this.restTemplate = restTemplateProvider.restTemplate();
    this.bingKey = bingKey;
    this.bingUrl = bingUrl.endsWith("/") ? bingUrl : bingUrl + "/";
  }

  private static Integer validateDriveTime(Integer val) {
    if (val != null && !DRIVE_TIME_VALUES.contains(val)) {
      throw new ApiExceptions.InvalidParameter("drive_time", val);
    }
    return val;
  }

  @SneakyThrows
  private Optional<DriveTimeBandEntity> firstIntersection(
      @NonNull Point2D point, List<DriveTimeBandEntity> entities) {
    Stopwatch timer = Stopwatch.createStarted();
    int count = 0;
    for (DriveTimeBandEntity entity : entities) {
      count++;
      Path2D path2D = toPath(entity);
      if (path2D.contains(point)) {
        log.info(
            "Found {} intersection in {} ms, looked at {} of {} options",
            entity.id().stationNumber(),
            timer.elapsed(TimeUnit.MILLISECONDS),
            count,
            entities.size());
        return Optional.of(entity);
      }
    }
    log.info("No matches found in {} options", entities.size());

    return Optional.empty();
  }

  @SneakyThrows
  private Coordinates geocodeAddress(
      @NonNull String street, @NonNull String city, @NonNull String state, @NonNull String zip) {
    String address = street + " " + city + " " + state + " " + zip;
    String bingUriString =
        UriComponentsBuilder.fromHttpUrl(bingUrl + "REST/v1/Locations")
            .queryParam("q", address)
            .queryParam("key", bingKey)
            .build()
            .toUriString();

    String body;
    try {
      body =
          restTemplate
              .exchange(
                  bingUriString, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class)
              .getBody();
    } catch (Exception ex) {
      throw new ApiExceptionsV0.BingException(ex);
    }
    if (isBlank(body)) {
      throw new ApiExceptionsV0.BingException("Empty response");
    }
    BingResponse response = JacksonConfig.createMapper().readValue(body, BingResponse.class);
    Optional<List<BigDecimal>> coordinates =
        response.resourceSets().stream()
            .flatMap(rs -> rs.resources().stream())
            .map(BingResponse.Resource::resourcePoint)
            .filter(Objects::nonNull)
            .map(BingResponse.Point::coordinates)
            .filter(c -> c.size() >= 2)
            .findFirst();

    if (coordinates.isEmpty()) {
      throw new ApiExceptionsV0.BingException(
          String.format(
              "Failed to geocode street_address '%s', city '%s', state '%s', zip '%s'",
              street, city, state, zip));
    }
    return Coordinates.builder()
        .latitude(coordinates.get().get(0))
        .longitude(coordinates.get().get(1))
        .build();
  }

  private String getMonthYearFromBandIds(List<NearbyId> ids) {
    String monthYear;

    if (!ids.isEmpty() && driveTimeBandRepository.findById(ids.get(0).bandId).isPresent()) {
      monthYear = driveTimeBandRepository.findById(ids.get(0).bandId).get().monthYear();
    } else {
      monthYear = driveTimeBandRepository.getDefaultBandVersion();
    }

    if (monthYear == null) {
      monthYear = "Unknown";
    }

    return monthYear;
  }

  private Map<String, DriveTimeBandEntity> intersections(
      @NonNull BigDecimal longitude,
      @NonNull BigDecimal latitude,
      List<DriveTimeBandEntity> entities) {
    ListMultimap<String, DriveTimeBandEntity> bandsForStation = ArrayListMultimap.create();
    for (DriveTimeBandEntity e : entities) {
      bandsForStation.put(e.id().stationNumber(), e);
    }

    Point2D point = new Point2D.Double(longitude.doubleValue(), latitude.doubleValue());
    return bandsForStation.asMap().entrySet().parallelStream()
        .map(
            entry -> {
              List<DriveTimeBandEntity> sortedEntities =
                  entry.getValue().stream()
                      .sorted(Comparator.comparingInt(left -> left.id().fromMinutes()))
                      .collect(toList());
              return firstIntersection(point, sortedEntities).orElse(null);
            })
        .filter(Objects::nonNull)
        .collect(toMap(b -> b.id().stationNumber(), Function.identity()));
  }

  /** Nearby facilities by address. */
  @GetMapping(
      produces = "application/json",
      params = {"street_address", "city", "state", "zip"})
  NearbyResponse nearbyAddress(
      @RequestParam(value = "street_address") String street,
      @RequestParam(value = "city") String city,
      @RequestParam(value = "state") String state,
      @RequestParam(value = "zip") String zip,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "drive_time", required = false) Integer maxDriveTime) {
    Coordinates coor = geocodeAddress(street, city, state, zip);
    List<NearbyId> ids = nearbyIds(coor.longitude(), coor.latitude(), services, maxDriveTime);

    return NearbyResponse.builder()
        .data(ids.stream().map(this::nearbyFacility).collect(toList()))
        .meta(NearbyResponse.Meta.builder().bandVersion(getMonthYearFromBandIds(ids)).build())
        .build();
  }

  private NearbyResponse.Nearby nearbyFacility(@NonNull NearbyId entity) {
    return NearbyResponse.Nearby.builder()
        .id(entity.facilityId())
        .type(NearbyResponse.Type.NearbyFacility)
        .attributes(
            NearbyResponse.NearbyAttributes.builder()
                .minTime(entity.bandId().fromMinutes())
                .maxTime(entity.bandId().toMinutes())
                .build())
        .build();
  }

  @SneakyThrows
  private List<NearbyId> nearbyIds(
      @NonNull BigDecimal longitude,
      @NonNull BigDecimal latitude,
      List<String> rawServices,
      Integer rawMaxDriveTime) {
    Set<Facility.ServiceType> services = validateServices(rawServices);
    Integer maxDriveTime = validateDriveTime(rawMaxDriveTime);
    log.info(
        "Searching near {},{} within {} minutes with {} services",
        longitude.doubleValue(),
        latitude.doubleValue(),
        maxDriveTime,
        services.size());
    var timer = Stopwatch.createStarted();
    List<DriveTimeBandEntity> maybeBands =
        driveTimeBandRepository.findAll(
            DriveTimeBandRepository.MinMaxSpecification.builder()
                .longitude(longitude)
                .latitude(latitude)
                .maxDriveTime(maxDriveTime)
                .build());
    log.info("{} bands found in {} ms", maybeBands.size(), timer.elapsed(TimeUnit.MILLISECONDS));
    Map<String, DriveTimeBandEntity> bandsByStation =
        intersections(longitude, latitude, maybeBands);
    List<FacilityEntity> facilityEntities =
        facilityRepository.findAll(
            FacilityRepository.StationNumbersSpecification.builder()
                .stationNumbers(bandsByStation.keySet())
                .facilityType(FacilityEntity.Type.vha)
                .services(services)
                .build());
    return facilityEntities.stream()
        .map(
            e ->
                NearbyId.builder()
                    .bandId(bandsByStation.get(e.id().stationNumber()).id())
                    .facilityId(e.id().toIdString())
                    .build())
        .sorted(Comparator.comparingInt(left -> left.bandId().toMinutes()))
        .collect(toList());
  }

  /** Nearby facilities by coordinates. */
  @GetMapping(
      produces = "application/json",
      params = {"lat", "lng"})
  NearbyResponse nearbyLatLong(
      @RequestParam(value = "lat") BigDecimal latitude,
      @RequestParam(value = "lng") BigDecimal longitude,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "drive_time", required = false) Integer maxDriveTime) {
    List<NearbyId> ids = nearbyIds(longitude, latitude, services, maxDriveTime);

    return NearbyResponse.builder()
        .data(ids.stream().map(this::nearbyFacility).collect(toList()))
        .meta(NearbyResponse.Meta.builder().bandVersion(getMonthYearFromBandIds(ids)).build())
        .build();
  }

  @SneakyThrows
  private Path2D toPath(DriveTimeBandEntity entity) {
    if (deprecatedPssgDriveTimeBandSupport.isPssgDriveTimeBand(entity)) {
      return deprecatedPssgDriveTimeBandSupport.toPath(entity);
    }
    try {
      return PathEncoder.create().decodeFromBase64(entity.band());
    } catch (Exception e) {
      log.info("Failed to decode {}", entity.id());
      throw e;
    }
  }

  @Builder
  @lombok.Value
  private static final class Coordinates {
    BigDecimal latitude;

    BigDecimal longitude;
  }

  /**
   * This encapsulates the older support where PSSG drive time bands were stored directly as JSON.
   * They were big and slow, and we have our own serialization model now. But to keep supporting any
   * records that have not be converted yet, this class allows for a graceful transition. It can
   * deleted once all databases have been upgraded.
   */
  private static final class DeprecatedPssgDriveTimeBandSupport {
    private final ObjectMapper mapper = JacksonConfig.createMapper();

    boolean isPssgDriveTimeBand(DriveTimeBandEntity entity) {
      return entity.band().startsWith("{\"attributes");
    }

    @SneakyThrows
    Path2D toPath(DriveTimeBandEntity entity) {
      PssgDriveTimeBand asBand = mapper.readValue(entity.band(), PssgDriveTimeBand.class);
      List<List<List<Double>>> rings = asBand.geometry().rings();
      checkState(!rings.isEmpty());
      List<List<Double>> exteriorRing = rings.get(0);
      Path2D path2D = toPath2D(exteriorRing);
      for (int i = 1; i < rings.size(); i++) {
        List<List<Double>> interiorRing = rings.get(i);
        path2D.append(toPath2D(interiorRing), false);
      }
      return path2D;
    }

    private Path2D toPath2D(List<List<Double>> coordinates) {
      checkArgument(!coordinates.isEmpty());
      Path2D shape = null;
      for (List<Double> c : coordinates) {
        if (shape == null) {
          shape = new Path2D.Double(Path2D.WIND_NON_ZERO);
          shape.moveTo(c.get(0), c.get(1));
        } else {
          shape.lineTo(c.get(0), c.get(1));
        }
      }
      shape.closePath();
      return shape;
    }
  }

  @Builder
  @lombok.Value
  private static final class NearbyId {
    DriveTimeBandEntity.Pk bandId;

    String facilityId;
  }
}
