package gov.va.api.lighthouse.facilities.collector;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.base.Stopwatch;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import us.dustinj.timezonemap.TimeZoneMap;

@Builder
@Slf4j
@SuppressWarnings("ObjectToString")
final class StateCemeteriesCollector {
  @NonNull final String baseUrl;

  @NonNull final RestTemplate insecureRestTemplate;

  @NonNull final Map<String, String> websites;

  @NonNull private final TimeZoneMap continentalUsTimeZoneMap;

  Collection<Facility> collect() {
    try {
      List<Facility> cemeteries =
          xmlCemeteries().stream()
              .filter(Objects::nonNull)
              .map(
                  c ->
                      StateCemeteryTransformer.builder()
                          .xml(c)
                          .websites(websites)
                          .continentalUsTimeZoneMap(continentalUsTimeZoneMap)
                          .build()
                          .toFacility())
              .filter(Objects::nonNull)
              .collect(toList());
      return cemeteries;
    } catch (Exception e) {
      throw new CollectorExceptions.StateCemeteriesCollectorException(e);
    }
  }

  @SneakyThrows
  private List<StateCemeteries.StateCemetery> xmlCemeteries() {
    Stopwatch totalWatch = Stopwatch.createStarted();
    String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "cems/cems.xml").build().toUriString();
    String response =
        insecureRestTemplate
            .exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class)
            .getBody();
    List<StateCemeteries.StateCemetery> cemeteries =
        new XmlMapper()
            .registerModule(new StringTrimModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .readValue(response, StateCemeteries.class)
            .cem();
    log.info(
        "Loading non-national cemeteries took {} millis for {} entries",
        totalWatch.stop().elapsed(TimeUnit.MILLISECONDS),
        cemeteries.size());
    checkState(!cemeteries.isEmpty(), "No cems.xml entries");
    return cemeteries;
  }

  private static final class StringTrimModule extends SimpleModule {
    StringTrimModule() {
      addDeserializer(
          String.class,
          new StdScalarDeserializer<String>(String.class) {
            @Override
            @SneakyThrows
            public String deserialize(JsonParser p, DeserializationContext ctxt) {
              return trimToNull(p.getValueAsString());
            }
          });
    }
  }
}
