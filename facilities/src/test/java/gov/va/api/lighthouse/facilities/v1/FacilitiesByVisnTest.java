package gov.va.api.lighthouse.facilities.v1;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.FacilityRepository;
import gov.va.api.lighthouse.facilities.FacilitySamples;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class FacilitiesByVisnTest {
  @Autowired private FacilityRepository repo;

  private FacilitiesControllerV1 controller() {
    return FacilitiesControllerV1.builder()
        .facilityRepository(repo)
        .baseUrl("http://foo/")
        .basePath("bp")
        .build();
  }

  @Test
  void geoFacilities() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(controller().geoFacilitiesByVisn("10", 1, 1))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(List.of(FacilitySamples.defaultSamples().geoFacility("vha_757")))
                .build());
  }

  @Test
  void json_searchVisn() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(controller().jsonFacilitiesByVisn("10", 1, 1).data())
        .isEqualTo(List.of(FacilitySamples.defaultSamples().facility("vha_757")));
  }
}
