package gov.va.api.lighthouse.facilities.v0;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.va.api.lighthouse.facilities.ApiExceptions;
import gov.va.api.lighthouse.facilities.FacilityRepository;
import gov.va.api.lighthouse.facilities.FacilitySamples;
import gov.va.api.lighthouse.facilities.api.v0.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.PageLinks;
import gov.va.api.lighthouse.facilities.api.v0.Pagination;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class FacilitiesByLatLongTest {
  @Autowired private FacilityRepository repo;

  private FacilitiesControllerV0 controller() {
    return FacilitiesControllerV0.builder()
        .facilityRepository(repo)
        .baseUrl("http://foo/")
        .basePath("")
        .build();
  }

  @Test
  void geoFacilities() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(
            controller()
                .geoFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    "HEALTH",
                    List.of("primarycare"),
                    false,
                    1,
                    10))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(
                    List.of(
                        FacilitySamples.defaultSamples().geoFacility("vha_757"),
                        FacilitySamples.defaultSamples().geoFacility("vha_740GA"),
                        FacilitySamples.defaultSamples().geoFacility("vha_691GB")))
                .build());
  }

  @Test
  void geoFacilities_ids() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(
            controller()
                .geoFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    "x,,xxx,,,,vha_757,vha_757,vha_757,xxxx,x",
                    "HEALTH",
                    List.of("primarycare"),
                    false,
                    1,
                    10))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(List.of(FacilitySamples.defaultSamples().geoFacility("vha_757")))
                .build());
  }

  @Test
  void json_ids() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    "x,,xxx,,,,vha_757,vha_757,vha_757,xxxx,x",
                    null,
                    null,
                    null,
                    1,
                    10)
                .data())
        .isEqualTo(List.of(FacilitySamples.defaultSamples().facility("vha_757")));
  }

  @Test
  void json_invalidService() {
    assertThrows(
        ApiExceptions.InvalidParameter.class,
        () ->
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    null,
                    List.of("unknown"),
                    null,
                    1,
                    1));
  }

  @Test
  void json_invalidType() {
    assertThrows(
        ApiExceptions.InvalidParameter.class,
        () ->
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    "xxx",
                    null,
                    null,
                    1,
                    1));
  }

  @Test
  void json_noFilter() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    null,
                    null,
                    null,
                    1,
                    10)
                .data())
        .isEqualTo(
            List.of(
                FacilitySamples.defaultSamples().facility("vha_757"),
                FacilitySamples.defaultSamples().facility("vha_740GA"),
                FacilitySamples.defaultSamples().facility("vha_691GB")));
  }

  @Test
  void json_perPageZero() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    null,
                    null,
                    null,
                    100,
                    0))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(emptyList())
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/v0/facilities?lat=28.112464&long=-80.7015994&page=100&per_page=0")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(100)
                                .entriesPerPage(0)
                                .totalPages(0)
                                .totalEntries(3)
                                .build())
                        .distances(emptyList())
                        .build())
                .build());
  }

  @Test
  void json_serviceOnly() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    null,
                    List.of("primarycare"),
                    null,
                    1,
                    10)
                .data())
        .isEqualTo(
            List.of(
                FacilitySamples.defaultSamples().facility("vha_757"),
                FacilitySamples.defaultSamples().facility("vha_740GA"),
                FacilitySamples.defaultSamples().facility("vha_691GB")));
  }

  @Test
  void json_typeAndService() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    String linkBase =
        "http://foo/v0/facilities?lat=28.112464&long=-80.7015994&services%5B%5D=primarycare&type=HEALTH";
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    "HEALTH",
                    List.of("primarycare"),
                    null,
                    1,
                    10))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(
                    List.of(
                        FacilitySamples.defaultSamples().facility("vha_757"),
                        FacilitySamples.defaultSamples().facility("vha_740GA"),
                        FacilitySamples.defaultSamples().facility("vha_691GB")))
                .links(
                    PageLinks.builder()
                        .self(linkBase + "&page=1&per_page=10")
                        .first(linkBase + "&page=1&per_page=10")
                        .last(linkBase + "&page=1&per_page=10")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(1)
                                .entriesPerPage(10)
                                .totalPages(1)
                                .totalEntries(3)
                                .build())
                        .distances(
                            List.of(
                                FacilitiesResponse.Distance.builder()
                                    .id("vha_757")
                                    .distance(new BigDecimal("829.69"))
                                    .build(),
                                FacilitiesResponse.Distance.builder()
                                    .id("vha_740GA")
                                    .distance(new BigDecimal("1050.77"))
                                    .build(),
                                FacilitiesResponse.Distance.builder()
                                    .id("vha_691GB")
                                    .distance(new BigDecimal("2333.84"))
                                    .build()))
                        .build())
                .build());
  }

  @Test
  void json_typeOnly() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_691GB"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_740GA"));
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(
            controller()
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    "HEALTH",
                    emptyList(),
                    null,
                    1,
                    10)
                .data())
        .isEqualTo(
            List.of(
                FacilitySamples.defaultSamples().facility("vha_757"),
                FacilitySamples.defaultSamples().facility("vha_740GA"),
                FacilitySamples.defaultSamples().facility("vha_691GB")));
  }
}
