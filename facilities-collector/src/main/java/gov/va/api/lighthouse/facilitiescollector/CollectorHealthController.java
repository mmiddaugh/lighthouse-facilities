package gov.va.api.lighthouse.facilitiescollector;

import static gov.va.api.lighthouse.facilitiescollector.Transformers.withTrailingSlash;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/collector", produces = "application/json")
public class CollectorHealthController {
  private final InsecureRestTemplateProvider insecureRestTemplateProvider;

  private final RestTemplate restTemplate;

  private final VastRepository vastRepository;

  private final String arcGisBaseUrl;

  private final String atcBaseUrl;

  private final String atpBaseUrl;

  private final String stateCemeteriesBaseUrl;

  private final AtomicBoolean hasCachedRecently = new AtomicBoolean(false);

  CollectorHealthController(
      @Autowired InsecureRestTemplateProvider insecureRestTemplateProvider,
      @Autowired RestTemplate restTemplate,
      @Autowired VastRepository vastRepository,
      @Value("${arc-gis.url}") String arcGisBaseUrl,
      @Value("${access-to-care.url}") String atcBaseUrl,
      @Value("${access-to-pwt.url}") String atpBaseUrl,
      @Value("${state-cemeteries.url}") String stateCemeteriesBaseUrl) {
    this.insecureRestTemplateProvider = insecureRestTemplateProvider;
    this.restTemplate = restTemplate;
    this.vastRepository = vastRepository;
    this.arcGisBaseUrl = withTrailingSlash(arcGisBaseUrl);
    this.atcBaseUrl = withTrailingSlash(atcBaseUrl);
    this.atpBaseUrl = withTrailingSlash(atpBaseUrl);
    this.stateCemeteriesBaseUrl = withTrailingSlash(stateCemeteriesBaseUrl);
  }

  private Supplier<Health> basicHealthCheck(HealthCheck healthCheck) {
    return () -> testDownstreamHealth(healthCheck);
  }

  private Health buildHealthFromStatusCode(String name, HttpStatus statusCode) {
    return Health.status(new Status(statusCode.is2xxSuccessful() ? "UP" : "DOWN", name))
        .withDetail("name", name)
        .withDetail("statusCode", statusCode.value())
        .withDetail("status", statusCode)
        .withDetail("time", Instant.now())
        .build();
  }

  /** Clears the cache every 5 minutes. */
  @Scheduled(cron = "0 */5 * * * *")
  @CacheEvict(value = {"health"})
  public void clearCacheScheduler() {
    if (hasCachedRecently.getAndSet(false)) {
      /* Help reduce log spam by only reporting cleared _after_ we've actually cached it. */
      log.info("Clearing downstream service health cache");
    }
  }

  /**
   * Gets health status of backend systems.
   *
   * <p>To ensure we are not introducing too much load on the downstream systems, we will cache the
   * results, limiting the interactions to once every 5 minutes. Spring Cacheable is used to record
   * the result, then periodically we invalidate the cache. See clearCacheScheduler() and
   * clearCache()
   */
  @GetMapping(value = "/health")
  @Cacheable("health")
  public ResponseEntity<Health> collectorHealth() {
    RequestContextHolder.setRequestAttributes(
        RequestContextHolder.currentRequestAttributes(), true);

    hasCachedRecently.set(true);
    var now = Instant.now();
    RestTemplate insecureTemplate = insecureRestTemplateProvider.restTemplate();
    List<Health> downstreamServices =
        List.of(
                basicHealthCheck(
                    HealthCheck.builder()
                        .restTemplate(insecureTemplate)
                        .name("Access to Care")
                        .url(
                            UriComponentsBuilder.fromHttpUrl(
                                    atcBaseUrl + "atcapis/v1.1/patientwaittimes")
                                .toUriString())
                        .build()),
                basicHealthCheck(
                    HealthCheck.builder()
                        .restTemplate(insecureTemplate)
                        .name("Access to PWT")
                        .url(
                            UriComponentsBuilder.fromHttpUrl(atpBaseUrl + "Shep/getRawData")
                                .queryParam("location", "FL")
                                .build()
                                .toUriString())
                        .build()),
                basicHealthCheck(
                    HealthCheck.builder()
                        .restTemplate(restTemplate)
                        .name("Public ArcGIS")
                        .url(
                            UriComponentsBuilder.fromHttpUrl(
                                    arcGisBaseUrl + "aqgBd3l68G8hEFFE/ArcGIS/rest/info/healthCheck")
                                .queryParam("f", "json")
                                .toUriString())
                        .build()),
                basicHealthCheck(
                    HealthCheck.builder()
                        .restTemplate(insecureTemplate)
                        .name("State Cemeteries")
                        .url(
                            UriComponentsBuilder.fromHttpUrl(
                                    stateCemeteriesBaseUrl + "cems/cems.xml")
                                .toUriString())
                        .build()),
                () -> testLastUpdated())
            .parallelStream()
            .map(Supplier::get)
            .collect(Collectors.toList());

    Health health =
        Health.status(
                new Status(
                    (downstreamServices.stream().anyMatch(d -> d.getStatus().equals(Status.DOWN))
                        ? "DOWN"
                        : "UP"),
                    "Downstream services"))
            .withDetail("name", "All downstream services")
            .withDetail("downstreamServices", downstreamServices)
            .withDetail("time", now)
            .build();
    log.info(health.toString());
    if (!health.getStatus().equals(Status.UP)) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(health);
    }
    return ResponseEntity.ok(health);
  }

  private ResponseEntity<String> requestHealth(RestTemplate rt, String url) {
    return rt.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
  }

  private Health testDownstreamHealth(HealthCheck healthCheck) {
    HttpStatus statusCode;
    try {
      statusCode = requestHealth(healthCheck.restTemplate(), healthCheck.url()).getStatusCode();
    } catch (ResourceAccessException e) {
      log.info("Exception occurred. GET {} message: {}", healthCheck.url(), e.getMessage());
      statusCode = HttpStatus.SERVICE_UNAVAILABLE;
    }
    return buildHealthFromStatusCode(healthCheck.name(), statusCode);
  }

  private Health testLastUpdated() {
    String statusCode = "DOWN";
    Instant lastUpdated = vastRepository.findLastUpdated();
    Instant now = Instant.now();
    Instant twentyFourHoursEarlier = now.minus(24, ChronoUnit.HOURS);
    // if ETL occured within 24 hours, OK
    if (!lastUpdated.isBefore(twentyFourHoursEarlier)) {
      statusCode = "UP";
    }
    return Health.status(new Status(statusCode, "ETL process succeeded in the last 24 hours"))
        .withDetail("name", "Facilities ETL process")
        .withDetail("time", Instant.now())
        .withDetail("lastUpdated", lastUpdated)
        .build();
  }

  @lombok.Value
  @Builder
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  private static class HealthCheck {
    RestTemplate restTemplate;

    String name;

    String url;
  }
}
