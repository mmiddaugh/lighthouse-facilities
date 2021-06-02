package gov.va.api.lighthouse.facilities.v0;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class FacilityEntityV0Test {
  @Test
  void pkFromIdStringParsesId() {
    assertThat(FacilityEntityV0.Pk.fromIdString("vba_A1B2C3"))
        .isEqualTo(FacilityEntityV0.Pk.of(FacilityEntityV0.Type.vba, "A1B2C3"));
    assertThat(FacilityEntityV0.Pk.fromIdString("vc_A1B2C3"))
        .isEqualTo(FacilityEntityV0.Pk.of(FacilityEntityV0.Type.vc, "A1B2C3"));
    assertThat(FacilityEntityV0.Pk.fromIdString("vha_A1B2C3"))
        .isEqualTo(FacilityEntityV0.Pk.of(FacilityEntityV0.Type.vha, "A1B2C3"));
    assertThat(FacilityEntityV0.Pk.fromIdString("nca_A1B2C3"))
        .isEqualTo(FacilityEntityV0.Pk.of(FacilityEntityV0.Type.nca, "A1B2C3"));

    assertThat(FacilityEntityV0.Pk.fromIdString("nca_A1_B2_C3"))
        .describedAs("station number with underscores")
        .isEqualTo(FacilityEntityV0.Pk.of(FacilityEntityV0.Type.nca, "A1_B2_C3"));

    assertThatIllegalArgumentException()
        .describedAs("ID with unknown type")
        .isThrownBy(() -> FacilityEntityV0.Pk.fromIdString("nope_A1B2C3"));
    assertThatIllegalArgumentException()
        .describedAs("ID without type")
        .isThrownBy(() -> FacilityEntityV0.Pk.fromIdString("_A1B2C3"));
    assertThatIllegalArgumentException()
        .describedAs("ID without station number")
        .isThrownBy(() -> FacilityEntityV0.Pk.fromIdString("vha_"));
    assertThatIllegalArgumentException()
        .describedAs("garbage ID")
        .isThrownBy(() -> FacilityEntityV0.Pk.fromIdString("vha"));
    assertThatIllegalArgumentException()
        .describedAs("garbage ID")
        .isThrownBy(() -> FacilityEntityV0.Pk.fromIdString("vha"));
    assertThatIllegalArgumentException()
        .describedAs("empty ID")
        .isThrownBy(() -> FacilityEntityV0.Pk.fromIdString(""));
  }

  @Test
  void servicesFromServiceTypes() {
    FacilityEntityV0 e = FacilityEntityV0.builder().build();
    e.servicesFromServiceTypes(
        Set.of(
            Facility.HealthService.SpecialtyCare,
            Facility.BenefitsService.ApplyingForBenefits,
            Facility.OtherService.OnlineScheduling));
    assertThat(e.services())
        .containsExactlyInAnyOrder(
            Facility.HealthService.SpecialtyCare.toString(),
            Facility.BenefitsService.ApplyingForBenefits.toString(),
            Facility.OtherService.OnlineScheduling.toString());
  }
}
