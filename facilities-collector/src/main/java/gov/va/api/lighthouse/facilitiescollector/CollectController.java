package gov.va.api.lighthouse.facilitiescollector;

import static com.google.common.base.Preconditions.checkState;
import static gov.va.api.lighthouse.facilitiescollector.Transformers.withTrailingSlash;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import gov.va.api.lighthouse.facilities.api.collector.CollectorFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Request Mapping to collect all facility information and output it in application/json format for
 * easy parsing.
 */
@Slf4j
@Validated
@RestController
@SuppressWarnings("WeakerAccess")
@RequestMapping(value = "/collect", produces = "application/json")
public class CollectController {
  private final InsecureRestTemplateProvider insecureRestTemplateProvider;

  private final JdbcTemplate jdbcTemplate;

  private final RestTemplate restTemplate;

  private final VastRepository vastRepository;

  private final String arcGisBaseUrl;

  private final String atcBaseUrl;

  private final String atpBaseUrl;

  private final String stateCemeteriesBaseUrl;

  /** Autowired constructor. */
  public CollectController(
      @Autowired InsecureRestTemplateProvider insecureRestTemplateProvider,
      @Autowired JdbcTemplate jdbcTemplate,
      @Autowired RestTemplate restTemplate,
      @Autowired VastRepository vastRepository,
      @Value("${arc-gis.url}") String arcGisBaseUrl,
      @Value("${access-to-care.url}") String atcBaseUrl,
      @Value("${access-to-pwt.url}") String atpBaseUrl,
      @Value("${state-cemeteries.url}") String stateCemeteriesBaseUrl) {
    this.insecureRestTemplateProvider = insecureRestTemplateProvider;
    this.jdbcTemplate = jdbcTemplate;
    this.restTemplate = restTemplate;
    this.vastRepository = vastRepository;
    this.arcGisBaseUrl = withTrailingSlash(arcGisBaseUrl);
    this.atcBaseUrl = withTrailingSlash(atcBaseUrl);
    this.atpBaseUrl = withTrailingSlash(atpBaseUrl);
    this.stateCemeteriesBaseUrl = withTrailingSlash(stateCemeteriesBaseUrl);
  }

  /** Loads the websites CSV file. */
  @SneakyThrows
  private static Map<String, String> loadWebsites() {
    final Stopwatch totalWatch = Stopwatch.createStarted();
    try (InputStreamReader reader =
        new InputStreamReader(
            new ClassPathResource("websites.csv").getInputStream(), StandardCharsets.UTF_8)) {
      Iterable<CSVRecord> rows = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
      Map<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
      for (CSVRecord row : rows) {
        String id = trimToNull(row.get(WebsiteCsvHeaders.id));
        String url = trimToNull(row.get(WebsiteCsvHeaders.url));
        checkState(id != null, "Website %s missing ID", url);
        checkState(url != null, "Website %s missing url", id);
        checkState(!map.containsKey(id), "Website %s duplicate", id);
        map.put(id, url);
      }
      Map<String, String> websites = Collections.unmodifiableMap(map);
      log.info(
          "Loading websites took {} millis for {} entries",
          totalWatch.stop().elapsed(TimeUnit.MILLISECONDS),
          websites.size());
      return websites;
    }
  }

  /** Request mapping for the /collect endpoint. */
  @SneakyThrows
  @GetMapping(value = "/facilities")
  public CollectorFacilitiesResponse collectFacilities() {
    Map<String, String> websites;
    Collection<VastEntity> vastEntities;
    try {
      websites = loadWebsites();
      vastEntities = loadVast();
    } catch (Exception e) {
      throw new CollectorExceptions.CollectorException(e);
    }

    Collection<Facility> healths =
        HealthsCollector.builder()
            .atcBaseUrl(atcBaseUrl)
            .atpBaseUrl(atpBaseUrl)
            .jdbcTemplate(jdbcTemplate)
            .insecureRestTemplate(insecureRestTemplateProvider.restTemplate())
            .vastEntities(vastEntities)
            .websites(websites)
            .build()
            .collect();

    Collection<Facility> stateCems =
        StateCemeteriesCollector.builder()
            .baseUrl(stateCemeteriesBaseUrl)
            .insecureRestTemplate(insecureRestTemplateProvider.restTemplate())
            .websites(websites)
            .build()
            .collect();

    Collection<Facility> vetCenters =
        VetCentersCollector.builder()
            .vastEntities(vastEntities)
            .websites(websites)
            .build()
            .collect();

    Collection<Facility> benefits =
        BenefitsCollector.builder()
            .arcgisUrl(arcGisBaseUrl)
            .restTemplate(restTemplate)
            .websites(websites)
            .build()
            .collect();

    Collection<Facility> cemeteries =
        CemeteriesCollector.builder()
            .arcgisUrl(arcGisBaseUrl)
            .restTemplate(restTemplate)
            .websites(websites)
            .build()
            .collect();

    log.info(
        "Collected: Health {},  Benefits {},  Vet Centers {}, State Cemeteries {}, Cemeteries {}",
        healths.size(),
        benefits.size(),
        vetCenters.size(),
        stateCems.size(),
        cemeteries.size());

    return CollectorFacilitiesResponse.builder()
        .facilities(
            Streams.stream(Iterables.concat(benefits, cemeteries, healths, stateCems, vetCenters))
                .sorted((left, right) -> left.id().compareToIgnoreCase(right.id()))
                .collect(Collectors.toList()))
        .build();
  }

  private List<VastEntity> loadVast() {
    Stopwatch watch = Stopwatch.createStarted();
    List<VastEntity> entities = ImmutableList.copyOf(vastRepository.findAll());
    log.info(
        "Loading VAST took {} millis for {} entries",
        watch.stop().elapsed(TimeUnit.MILLISECONDS),
        entities.size());
    return entities;
  }

  private enum WebsiteCsvHeaders {
    id,
    url
  }
}
