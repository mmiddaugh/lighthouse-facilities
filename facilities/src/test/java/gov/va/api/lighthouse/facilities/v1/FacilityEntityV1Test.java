package gov.va.api.lighthouse.facilities.v1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import gov.va.api.lighthouse.facilities.api.v1.Facility;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class FacilityEntityV1Test {
  @Test
  void pkFromIdStringParsesId() {
    assertThat(FacilityEntityV1.Pk.fromIdString("vba_A1B2C3"))
        .isEqualTo(FacilityEntityV1.Pk.of(FacilityEntityV1.Type.vba, "A1B2C3"));
    assertThat(FacilityEntityV1.Pk.fromIdString("vc_A1B2C3"))
        .isEqualTo(FacilityEntityV1.Pk.of(FacilityEntityV1.Type.vc, "A1B2C3"));
    assertThat(FacilityEntityV1.Pk.fromIdString("vha_A1B2C3"))
        .isEqualTo(FacilityEntityV1.Pk.of(FacilityEntityV1.Type.vha, "A1B2C3"));
    assertThat(FacilityEntityV1.Pk.fromIdString("nca_A1B2C3"))
        .isEqualTo(FacilityEntityV1.Pk.of(FacilityEntityV1.Type.nca, "A1B2C3"));

    assertThat(FacilityEntityV1.Pk.fromIdString("nca_A1_B2_C3"))
        .describedAs("station number with underscores")
        .isEqualTo(FacilityEntityV1.Pk.of(FacilityEntityV1.Type.nca, "A1_B2_C3"));

    assertThatIllegalArgumentException()
        .describedAs("ID with unknown type")
        .isThrownBy(() -> FacilityEntityV1.Pk.fromIdString("nope_A1B2C3"));
    assertThatIllegalArgumentException()
        .describedAs("ID without type")
        .isThrownBy(() -> FacilityEntityV1.Pk.fromIdString("_A1B2C3"));
    assertThatIllegalArgumentException()
        .describedAs("ID without station number")
        .isThrownBy(() -> FacilityEntityV1.Pk.fromIdString("vha_"));
    assertThatIllegalArgumentException()
        .describedAs("garbage ID")
        .isThrownBy(() -> FacilityEntityV1.Pk.fromIdString("vha"));
    assertThatIllegalArgumentException()
        .describedAs("garbage ID")
        .isThrownBy(() -> FacilityEntityV1.Pk.fromIdString("vha"));
    assertThatIllegalArgumentException()
        .describedAs("empty ID")
        .isThrownBy(() -> FacilityEntityV1.Pk.fromIdString(""));
  }

  @Test
  void servicesFromServiceTypes() {
    FacilityEntityV1 e = FacilityEntityV1.builder().build();
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
