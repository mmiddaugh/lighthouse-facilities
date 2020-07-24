package gov.va.api.lighthouse.facilitiescollector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.collector.CollectorFacilitiesResponse;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

public class CollectContollerTest {
  @Test
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public void verifyResponse() {
    RestTemplate restTemplate = mock(RestTemplate.class);
    RestTemplate insecureRestTemplate = mock(RestTemplate.class);

    when(restTemplate.exchange(
            contains("VBA_Facilities"),
            eq(HttpMethod.GET),
            Mockito.any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(
            ResponseEntity.of(
                Optional.of(
                    JacksonConfig.createMapper()
                        .writeValueAsString(ArcGisBenefits.builder().build()))));

    when(restTemplate.exchange(
            contains("NCA_Facilities"),
            eq(HttpMethod.GET),
            Mockito.any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(
            ResponseEntity.of(
                Optional.of(
                    JacksonConfig.createMapper()
                        .writeValueAsString(ArcGisCemeteries.builder().build()))));

    when(insecureRestTemplate.exchange(
            startsWith("http://atc/atcapis"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(
            ResponseEntity.of(
                Optional.of(
                    JacksonConfig.createMapper()
                        .writeValueAsString(
                            List.of(AccessToCareEntry.builder().facilityId("x").build())))));

    when(insecureRestTemplate.exchange(
            startsWith("http://atp"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(
            ResponseEntity.of(
                Optional.of(
                    JacksonConfig.createMapper()
                        .writeValueAsString(
                            List.of(AccessToPwtEntry.builder().facilityId("x").build())))));

    when(insecureRestTemplate.exchange(
            startsWith("http://statecems"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(ResponseEntity.of(Optional.of("<cems></cems>")));

    InsecureRestTemplateProvider insecureRestTemplateProvider =
        mock(InsecureRestTemplateProvider.class);
    when(insecureRestTemplateProvider.restTemplate()).thenReturn(insecureRestTemplate);

    assertThat(
            new CollectController(
                    insecureRestTemplateProvider,
                    mock(JdbcTemplate.class),
                    restTemplate,
                    mock(VastRepository.class),
                    "http://arcgis",
                    "http://atc",
                    "http://atp",
                    "http://statecems")
                .collectFacilities())
        .isExactlyInstanceOf(CollectorFacilitiesResponse.class);
  }
}
