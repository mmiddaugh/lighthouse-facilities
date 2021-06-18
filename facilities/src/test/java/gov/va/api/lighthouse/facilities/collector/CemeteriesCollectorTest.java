package gov.va.api.lighthouse.facilities.collector;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.client.RestTemplate;

public class CemeteriesCollectorTest {
  @Test
  @SneakyThrows
  void collect() {
    RestTemplate insecureRestTemplate = mock(RestTemplate.class);

    ResponseEntity<String> response = mock(ResponseEntity.class);
    when(response.getBody())
        .thenReturn(
            new XmlMapper()
                .writeValueAsString(
                    NationalCemeteries.builder()
                        .cem(
                            List.of(
                                NationalCemeteries.NationalCemetery.builder()
                                    .id("1001")
                                    .url("http://www.va.state.al.us/spanishfort.aspx")
                                    .build()))
                        .build()));

    when(insecureRestTemplate.exchange(
            startsWith("http://nationalcems"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(response);

    JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

    when(jdbcTemplate.query(any(String.class), any(RowMapper.class)))
        .thenReturn(
            List.of(
                CdwCemetery.builder()
                    .siteId("088")
                    .fullName("Albany Rural")
                    .siteType("Rural")
                    .siteAddress1("Cemetery Avenue")
                    .siteAddress2("<Null>")
                    .siteCity("Albany")
                    .siteState("NY")
                    .siteZip("12204")
                    .mailAddress1("200 Duell Road")
                    .mailAddress2("")
                    .mailCity("Schuylerville")
                    .mailState("NY")
                    .mailZip("12871-1721")
                    .phone("")
                    .fax("5184630787")
                    .visitationHoursWeekday("Sunrise - Sundown")
                    .visitationHoursWeekend("Sunrise - Sundown")
                    .latitude(new BigDecimal("42.703844900000036"))
                    .longitude(new BigDecimal("-73.72356499999995"))
                    .websiteUrl("http://www.testme.com/")
                    .build()));

    assertThat(
            CemeteriesCollector.builder()
                .baseUrl("http://nationalcems")
                .insecureRestTemplate(insecureRestTemplate)
                .websites(new HashMap<>())
                .timeZoneMap(CalculateTimeZone.getEverywhereTimeZoneMap())
                .jdbcTemplate(jdbcTemplate)
                .build()
                .collect())
        .isEqualTo(
            List.of(
                Facility.builder()
                    .id("nca_088")
                    .type(Facility.Type.va_facilities)
                    .attributes(
                        Facility.FacilityAttributes.builder()
                            .name("Albany Rural")
                            .facilityType(Facility.FacilityType.va_cemetery)
                            .classification("Rural")
                            .latitude(new BigDecimal("42.703844900000036"))
                            .longitude(new BigDecimal("-73.72356499999995"))
                            .timeZone("America/New_York")
                            .address(
                                Facility.Addresses.builder()
                                    .physical(
                                        Facility.Address.builder()
                                            .address1("Cemetery Avenue")
                                            .address2(null)
                                            .city("Albany")
                                            .state("NY")
                                            .zip("12204")
                                            .build())
                                    .mailing(
                                        Facility.Address.builder()
                                            .address1("200 Duell Road")
                                            .address2("")
                                            .city("Schuylerville")
                                            .state("NY")
                                            .zip("12871-1721")
                                            .build())
                                    .build())
                            .phone(Facility.Phone.builder().fax("5184630787").main(null).build())
                            .hours(
                                Facility.Hours.builder()
                                    .monday("Sunrise - Sundown")
                                    .tuesday("Sunrise - Sundown")
                                    .wednesday("Sunrise - Sundown")
                                    .thursday("Sunrise - Sundown")
                                    .friday("Sunrise - Sundown")
                                    .saturday("Sunrise - Sundown")
                                    .sunday("Sunrise - Sundown")
                                    .build())
                            .website("http://www.testme.com/")
                            .build())
                    .build()));
  }

  @Test
  @SneakyThrows
  void exception() {
    RestTemplate insecureRestTemplate = mock(RestTemplate.class);

    ResponseEntity<String> response = mock(ResponseEntity.class);
    when(response.getBody())
        .thenReturn(
            new XmlMapper()
                .writeValueAsString(
                    NationalCemeteries.builder()
                        .cem(
                            List.of(
                                NationalCemeteries.NationalCemetery.builder()
                                    .id("1001")
                                    .url("http://www.va.state.al.us/spanishfort.aspx")
                                    .build()))
                        .build()));

    when(insecureRestTemplate.exchange(
            startsWith("http://wrong"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(response);
    assertThrows(
        CollectorExceptions.CemeteriesCollectorException.class,
        () ->
            CemeteriesCollector.builder()
                .baseUrl("http://wrong")
                .insecureRestTemplate(insecureRestTemplate)
                .websites(emptyMap())
                .timeZoneMap(CalculateTimeZone.getEverywhereTimeZoneMap())
                .build()
                .collect());
  }

  @Test
  @SneakyThrows
  void toCdwCemetery() throws SQLException {
    ResultSet resultSet = mock(ResultSet.class);

    assertThat(CemeteriesCollector.toCdwCemetery(resultSet))
        .isEqualTo(CdwCemetery.builder().build());
  }
}
