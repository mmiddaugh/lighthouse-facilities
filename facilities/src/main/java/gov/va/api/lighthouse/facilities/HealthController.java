package gov.va.api.lighthouse.facilities;

import gov.va.api.lighthouse.facilities.collector.InsecureRestTemplateProvider;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping(produces = "application/json")
public class HealthController {
  private final FacilityRepository repository;

  private final InsecureRestTemplateProvider insecureRestTemplateProvider;

  private final JdbcTemplate jdbcTemplate;

  private final RestTemplate restTemplate;

  private final String arcGisBaseUrl;

  private final String atcBaseUrl;

  private final String atpBaseUrl;

  private final String stateCemeteriesBaseUrl;

  private final AtomicBoolean hasCachedCollectorBackend = new AtomicBoolean(false);

  private final AtomicBoolean hasCachedCollectionStatus = new AtomicBoolean(false);

  HealthController(
      @Autowired FacilityRepository repository,
      @Autowired InsecureRestTemplateProvider insecureRestTemplateProvider,
      @Autowired JdbcTemplate jdbcTemplate,
      @Autowired RestTemplate restTemplate,
      @Value("${arc-gis.url}") String arcGisBaseUrl,
      @Value("${access-to-care.url}") String atcBaseUrl,
      @Value("${access-to-pwt.url}") String atpBaseUrl,
      @Value("${state-cemeteries.url}") String stateCemeteriesBaseUrl) {
    this.repository = repository;
    this.insecureRestTemplateProvider = insecureRestTemplateProvider;
    this.jdbcTemplate = jdbcTemplate;
    this.restTemplate = restTemplate;
    this.arcGisBaseUrl = withTrailingSlash(arcGisBaseUrl);
    this.atcBaseUrl = withTrailingSlash(atcBaseUrl);
    this.atpBaseUrl = withTrailingSlash(atpBaseUrl);
    this.stateCemeteriesBaseUrl = withTrailingSlash(stateCemeteriesBaseUrl);
  }

  private static Health testHealth(
      @NonNull Instant now,
      @NonNull RestTemplate restTemplate,
      @NonNull String name,
      @NonNull String url) {
    HttpStatus statusCode;
    try {
      statusCode =
          restTemplate
              .exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class)
              .getStatusCode();
    } catch (ResourceAccessException e) {
      log.info("Exception occurred. GET {} message: {}", url, e.getMessage());
      statusCode = HttpStatus.SERVICE_UNAVAILABLE;
    }
    return Health.status(new Status(statusCode.is2xxSuccessful() ? "UP" : "DOWN", name))
        .withDetail("name", name)
        .withDetail("statusCode", statusCode.value())
        .withDetail("status", statusCode)
        .withDetail("time", now)
        .build();
  }

  private static String withTrailingSlash(@NonNull String url) {
    return url.endsWith("/") ? url : url + "/";
  }

  /** Clear cache every 5 minutes. */
  @Scheduled(cron = "0 */5 * * * *")
  @CacheEvict(value = "collection-status")
  public void clearCollectionStatusScheduler() {
    if (hasCachedCollectionStatus.getAndSet(false)) {
      // reduce log spam by only reporting cleared if we've actually cached something
      log.info("Clearing facilities-collection-status cache");
    }
  }

  /** Clear cache every 5 minutes. */
  @Scheduled(cron = "0 */5 * * * *")
  @CacheEvict(value = "collector-backend-health")
  public void clearCollectorBackendHealthScheduler() {
    if (hasCachedCollectorBackend.getAndSet(false)) {
      // reduce log spam by only reporting cleared if we've actually cached something
      log.info("Clearing facilities-collector-backend-health cache");
    }
  }

  /** Get health of last facilities reload. */
  @Cacheable("collection-status")
  @GetMapping("/collection/status")
  public ResponseEntity<Health> collectionStatusHealth() {
    hasCachedCollectionStatus.set(true);
    Instant now = Instant.now();
    Health reloadLastUpdatedHealth = testReloadLastUpdated(now);
    log.info(reloadLastUpdatedHealth.toString());
    HttpStatus httpStatus =
        reloadLastUpdatedHealth.getStatus().equals(Status.UP)
            ? HttpStatus.OK
            : HttpStatus.SERVICE_UNAVAILABLE;
    return ResponseEntity.status(httpStatus).body(reloadLastUpdatedHealth);
  }

  /**
   * Gets health of collector backend systems.
   *
   * <p>To ensure we are not introducing too much load on downstream systems, cache the results,
   * limiting interactions to once every five minutes.
   *
   * <p>Spring Cacheable is used to record the result. Periodically we invalidate the cache.
   */
  @Cacheable("collector-backend-health")
  @GetMapping(value = "/collector/health")
  public ResponseEntity<Health> collectorBackendHealth() {
    hasCachedCollectorBackend.set(true);
    var now = Instant.now();
    RestTemplate insecureTemplate = insecureRestTemplateProvider.restTemplate();
    List<Health> healths = new ArrayList<>(5);
    healths.add(
        testHealth(
            now,
            insecureTemplate,
            "Access to Care",
            UriComponentsBuilder.fromHttpUrl(atcBaseUrl + "atcapis/v1.1/patientwaittimes")
                .toUriString()));
    healths.add(
        testHealth(
            now,
            insecureTemplate,
            "Access to PWT",
            UriComponentsBuilder.fromHttpUrl(atpBaseUrl + "Shep/getRawData")
                .queryParam("location", "FL")
                .build()
                .toUriString()));
    healths.add(
        testHealth(
            now,
            restTemplate,
            "Public ArcGIS",
            UriComponentsBuilder.fromHttpUrl(
                    arcGisBaseUrl + "aqgBd3l68G8hEFFE/ArcGIS/rest/info/healthCheck")
                .queryParam("f", "json")
                .toUriString()));
    healths.add(
        testHealth(
            now,
            insecureTemplate,
            "State Cemeteries",
            UriComponentsBuilder.fromHttpUrl(stateCemeteriesBaseUrl + "cems/cems.xml")
                .toUriString()));
    healths.add(testEtlLastUpdated(now));
    Health overallHealth =
        Health.status(
                new Status(
                    healths.stream().anyMatch(h -> h.getStatus().equals(Status.DOWN))
                        ? "DOWN"
                        : "UP",
                    "Downstream services"))
            .withDetail("name", "All downstream services")
            .withDetail("downstreamServices", healths)
            .withDetail("time", now)
            .build();
    log.info(overallHealth.toString());
    if (!overallHealth.getStatus().equals(Status.UP)) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(overallHealth);
    }
    return ResponseEntity.ok(overallHealth);
  }

  private Health testEtlLastUpdated(@NonNull Instant now) {
    Instant lastUpdated =
        Optional.ofNullable(
                jdbcTemplate.queryForObject(
                    "SELECT MAX(LASTUPDATED) FROM App.Vast", Timestamp.class))
            .map(t -> t.toInstant())
            .orElse(null);

    if (lastUpdated == null) {
      return Health.status(new Status("DOWN", "ETL process succeeded in the last 24 hours"))
          .withDetail("name", "Facilities ETL process")
          .withDetail("time", now)
          .build();
    } else {
      Instant twentyFourHoursEarlier = now.minus(24, ChronoUnit.HOURS);
      String statusCode = !lastUpdated.isBefore(twentyFourHoursEarlier) ? "UP" : "DOWN";

      return Health.status(new Status(statusCode, "ETL process succeeded in the last 24 hours"))
          .withDetail("name", "Facilities ETL process")
          .withDetail("time", now)
          .withDetail("lastUpdated", lastUpdated)
          .build();
    }
  }

  private Health testReloadLastUpdated(@NonNull Instant now) {
    Instant lastSuccessfulUpdate = repository.findLastUpdated();
    if (lastSuccessfulUpdate == null) {
      return Health.status(new Status("DOWN", "Facilities Collection"))
          .withDetail("name", "Facilities Collection")
          .withDetail("time", now)
          .build();
    }
    Instant oldestValidDate = now.minus(24, ChronoUnit.HOURS);
    boolean isOutOfDate = oldestValidDate.isAfter(lastSuccessfulUpdate);
    return Health.status(new Status(!isOutOfDate ? "UP" : "DOWN", "Facilities Collection"))
        .withDetail("name", "Facilities Collection")
        .withDetail("lastUpdated", lastSuccessfulUpdate)
        .withDetail("time", now)
        .build();
  }
}