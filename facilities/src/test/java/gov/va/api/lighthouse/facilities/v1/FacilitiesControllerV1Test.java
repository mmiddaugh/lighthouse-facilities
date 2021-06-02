package gov.va.api.lighthouse.facilities.v1;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import gov.va.api.lighthouse.facilities.ApiExceptions;
import gov.va.api.lighthouse.facilities.CsvTransformer;
import gov.va.api.lighthouse.facilities.DriveTimeBandRepository;
import gov.va.api.lighthouse.facilities.FacilitiesJacksonConfig;
import gov.va.api.lighthouse.facilities.FacilityRepository;
import gov.va.api.lighthouse.facilities.FacilitySamples;
import gov.va.api.lighthouse.facilities.api.v0.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacility;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.PageLinks;
import gov.va.api.lighthouse.facilities.api.v0.Pagination;
import gov.va.api.lighthouse.facilities.v0.FacilityEntityV0;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class FacilitiesControllerV1Test {
  FacilityRepository fr = mock(FacilityRepository.class);

  DriveTimeBandRepository dbr = mock(DriveTimeBandRepository.class);

  @Test
  @SneakyThrows
  void all() {
    FacilitySamples samples = FacilitySamples.defaultSamples();
    when(fr.findAllProjectedBy())
        .thenReturn(
            List.of(
                samples.facilityEntity("vha_691GB"),
                samples.facilityEntity("vha_740GA"),
                samples.facilityEntity("vha_757")));
    String actual = controller().all();
    Assertions.assertThat(
            FacilitiesJacksonConfig.createMapper()
                .readValue(actual, GeoFacilitiesResponse.class)
                .features())
        .hasSize(3);
  }

  @Test
  void allCsv() {
    FacilitySamples samples = FacilitySamples.defaultSamples();
    when(fr.findAllProjectedBy())
        .thenReturn(
            List.of(
                samples.facilityEntity("vha_691GB"),
                samples.facilityEntity("vha_740GA"),
                samples.facilityEntity("vha_757")));
    String actual = controller().allCsv();
    List<String> actualLines = Splitter.onPattern("\\r?\\n").omitEmptyStrings().splitToList(actual);
    assertThat(actualLines.size()).isEqualTo(4);
    assertThat(actualLines.get(0)).isEqualTo(Joiner.on(",").join(CsvTransformer.HEADERS));
    assertThat(actualLines.get(1))
        .isEqualTo(
            "vha_691GB,Santa Barbara VA Clinic,691GB,34.4423637,-119.77646693,va_health_facility,"
                + "Primary Care CBOC,https://www.losangeles.va.gov/locations/directions-SB.asp,false,A,"
                + "22,4440 Calle Real,,,Santa Barbara,CA,93110-1002,,,,,,,805-683-1491,805-683-3631,"
                + "310-268-4449,800-952-4852,877-252-4866,818-895-9564,818-891-7711 x35894,800AM-430PM,"
                + "800AM-430PM,800AM-430PM,800AM-430PM,800AM-430PM,Closed,Closed,NORMAL,\"all day, every day\"");
    assertThat(actualLines.get(2))
        .isEqualTo(
            "vha_740GA,Harlingen VA Clinic-Treasure Hills,740GA,26.1745479800001,-97.6667188,va_health_facility,"
                + "Multi-Specialty CBOC,https://www.texasvalley.va.gov/locations/Harlingen_OPC.asp,false,A,"
                + "17,2106 Treasure Hills Boulevard,,,Harlingen,TX,78550-8736,,,,,,,956-366-4500,956-366-4595,"
                + "956-366-4526,877-752-0650,888-686-6350,956-366-4500 x67810,956-291-9791,800AM-430PM,"
                + "800AM-430PM,800AM-430PM,800AM-430PM,800AM-430PM,Closed,Closed,NORMAL,");
    assertThat(actualLines.get(3))
        .isEqualTo(
            "vha_757,Chalmers P. Wylie Veterans Outpatient Clinic,757,39.9813738,-82.9118322899999,va_health_facility,"
                + "Health Care Center (HCC),https://www.columbus.va.gov/locations/directions.asp,false,A,"
                + "10,\"420 North James, Road\",,,Columbus,OH,43219-1834,,,,,,,614-257-5200,614-257-5460,"
                + "614-257-5631,614-257-5230,614-257-5512,614-257-5290,614-257-5298,730AM-600PM,"
                + "730AM-600PM,730AM-600PM,730AM-600PM,730AM-600PM,800AM-400PM,800AM-400PM,NORMAL,");
  }

  private FacilitiesControllerV1 controller() {
    return FacilitiesControllerV1.builder()
        .facilityRepository(fr)
        .baseUrl("http://foo/")
        .basePath("bp")
        .build();
  }

  @Test
  void geoFacilitiesByIds() {
    when(fr.findByIdIn(
            List.of(
                FacilityEntityV0.Pk.of(FacilityEntityV0.Type.vha, "691GB"),
                FacilityEntityV0.Pk.of(FacilityEntityV0.Type.vha, "740GA"),
                FacilityEntityV0.Pk.of(FacilityEntityV0.Type.vha, "757"))))
        .thenReturn(
            List.of(
                FacilitySamples.defaultSamples().facilityEntity("vha_757"),
                FacilitySamples.defaultSamples().facilityEntity("vha_740GA"),
                FacilitySamples.defaultSamples().facilityEntity("vha_691GB")));
    assertThat(controller().geoFacilitiesByIds("x,vha_691GB,,x,,vha_740GA,vha_757", 2, 1))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(List.of(FacilitySamples.defaultSamples().geoFacility("vha_740GA")))
                .build());
  }

  @Test
  void jsonFacilitiesByIds() {
    when(fr.findByIdIn(
            List.of(
                FacilityEntityV0.Pk.of(FacilityEntityV0.Type.vha, "691GB"),
                FacilityEntityV0.Pk.of(FacilityEntityV0.Type.vha, "740GA"),
                FacilityEntityV0.Pk.of(FacilityEntityV0.Type.vha, "757"))))
        .thenReturn(
            List.of(
                FacilitySamples.defaultSamples().facilityEntity("vha_740GA"),
                FacilitySamples.defaultSamples().facilityEntity("vha_691GB"),
                FacilitySamples.defaultSamples().facilityEntity("vha_757")));
    assertThat(controller().jsonFacilitiesByIds("x,vha_691GB,,x,,vha_740GA,vha_757", 2, 1))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(List.of(FacilitySamples.defaultSamples().facility("vha_740GA")))
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/bp/v0/facilities?ids=x%2Cvha_691GB%2C%2Cx%2C%2Cvha_740GA%2Cvha_757&page=2&per_page=1")
                        .first(
                            "http://foo/bp/v0/facilities?ids=x%2Cvha_691GB%2C%2Cx%2C%2Cvha_740GA%2Cvha_757&page=1&per_page=1")
                        .prev(
                            "http://foo/bp/v0/facilities?ids=x%2Cvha_691GB%2C%2Cx%2C%2Cvha_740GA%2Cvha_757&page=1&per_page=1")
                        .next(
                            "http://foo/bp/v0/facilities?ids=x%2Cvha_691GB%2C%2Cx%2C%2Cvha_740GA%2Cvha_757&page=3&per_page=1")
                        .last(
                            "http://foo/bp/v0/facilities?ids=x%2Cvha_691GB%2C%2Cx%2C%2Cvha_740GA%2Cvha_757&page=3&per_page=1")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(2)
                                .entriesPerPage(1)
                                .totalPages(3)
                                .totalEntries(3)
                                .build())
                        .build())
                .build());
  }

  @Test
  void jsonFacilitiesByIds_perPageZero() {
    when(fr.findByIdIn(
            List.of(
                FacilityEntityV0.Pk.of(FacilityEntityV0.Type.vha, "691GB"),
                FacilityEntityV0.Pk.of(FacilityEntityV0.Type.vha, "740GA"),
                FacilityEntityV0.Pk.of(FacilityEntityV0.Type.vha, "757"))))
        .thenReturn(
            List.of(
                FacilitySamples.defaultSamples().facilityEntity("vha_691GB"),
                FacilitySamples.defaultSamples().facilityEntity("vha_740GA"),
                FacilitySamples.defaultSamples().facilityEntity("vha_757")));
    assertThat(controller().jsonFacilitiesByIds("x,vha_691GB,,x,,vha_740GA,vha_757", 2, 0))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(emptyList())
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/bp/v0/facilities?ids=x%2Cvha_691GB%2C%2Cx%2C%2Cvha_740GA%2Cvha_757&page=2&per_page=0")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(2)
                                .entriesPerPage(0)
                                .totalPages(0)
                                .totalEntries(3)
                                .build())
                        .build())
                .build());
  }

  @Test
  void readGeoJson() {
    GeoFacility geo = FacilitySamples.defaultSamples().geoFacility("vha_691GB");
    FacilityEntityV0 entity = FacilitySamples.defaultSamples().facilityEntity("vha_691GB");
    when(fr.findById(FacilityEntityV0.Pk.of(FacilityEntityV0.Type.vha, "691GB")))
        .thenReturn(Optional.of(entity));
    assertThat(controller().readGeoJson("vha_691GB")).isEqualTo(GeoFacilityReadResponse.of(geo));
  }

  @Test
  void readJson() {
    Facility facility = FacilitySamples.defaultSamples().facility("vha_691GB");
    FacilityEntityV0 entity = FacilitySamples.defaultSamples().facilityEntity("vha_691GB");
    when(fr.findById(FacilityEntityV0.Pk.of(FacilityEntityV0.Type.vha, "691GB")))
        .thenReturn(Optional.of(entity));
    assertThat(controller().readJson("vha_691GB"))
        .isEqualTo(FacilityReadResponse.builder().facility(facility).build());
  }

  @Test
  void readJson_malformed() {
    assertThrows(ApiExceptions.NotFound.class, () -> controller().readJson("xxx"));
  }

  @Test
  void readJson_notFound() {
    assertThrows(ApiExceptions.NotFound.class, () -> controller().readJson("vha_691GB"));
  }
}
