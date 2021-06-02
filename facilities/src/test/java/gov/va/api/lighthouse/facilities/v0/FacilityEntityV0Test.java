package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import gov.va.api.lighthouse.facilities.api.v0.FacilityV0;
import gov.va.api.lighthouse.facilities.v0.FacilityEntity;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class FacilityV0EntityTest {
  @Test
  void pkFromIdStringParsesId() {
    assertThat(FacilityEntity.Pk.fromIdString("vba_A1B2C3"))
        .isEqualTo(FacilityEntity.Pk.of(FacilityEntity.Type.vba, "A1B2C3"));
    assertThat(FacilityEntity.Pk.fromIdString("vc_A1B2C3"))
        .isEqualTo(FacilityEntity.Pk.of(FacilityEntity.Type.vc, "A1B2C3"));
    assertThat(FacilityEntity.Pk.fromIdString("vha_A1B2C3"))
        .isEqualTo(FacilityEntity.Pk.of(FacilityEntity.Type.vha, "A1B2C3"));
    assertThat(FacilityEntity.Pk.fromIdString("nca_A1B2C3"))
        .isEqualTo(FacilityEntity.Pk.of(FacilityEntity.Type.nca, "A1B2C3"));

    assertThat(FacilityEntity.Pk.fromIdString("nca_A1_B2_C3"))
        .describedAs("station number with underscores")
        .isEqualTo(FacilityEntity.Pk.of(FacilityEntity.Type.nca, "A1_B2_C3"));

    assertThatIllegalArgumentException()
        .describedAs("ID with unknown type")
        .isThrownBy(() -> FacilityEntity.Pk.fromIdString("nope_A1B2C3"));
    assertThatIllegalArgumentException()
        .describedAs("ID without type")
        .isThrownBy(() -> FacilityEntity.Pk.fromIdString("_A1B2C3"));
    assertThatIllegalArgumentException()
        .describedAs("ID without station number")
        .isThrownBy(() -> FacilityEntity.Pk.fromIdString("vha_"));
    assertThatIllegalArgumentException()
        .describedAs("garbage ID")
        .isThrownBy(() -> FacilityEntity.Pk.fromIdString("vha"));
    assertThatIllegalArgumentException()
        .describedAs("garbage ID")
        .isThrownBy(() -> FacilityEntity.Pk.fromIdString("vha"));
    assertThatIllegalArgumentException()
        .describedAs("empty ID")
        .isThrownBy(() -> FacilityEntity.Pk.fromIdString(""));
  }

  @Test
  void servicesFromServiceTypes() {
    FacilityEntity e = FacilityEntity.builder().build();
    e.servicesFromServiceTypes(
        Set.of(
            FacilityV0.HealthService.SpecialtyCare,
            FacilityV0.BenefitsService.ApplyingForBenefits,
            FacilityV0.OtherService.OnlineScheduling));
    assertThat(e.services())
        .containsExactlyInAnyOrder(
            FacilityV0.HealthService.SpecialtyCare.toString(),
            FacilityV0.BenefitsService.ApplyingForBenefits.toString(),
            FacilityV0.OtherService.OnlineScheduling.toString());
  }
}
