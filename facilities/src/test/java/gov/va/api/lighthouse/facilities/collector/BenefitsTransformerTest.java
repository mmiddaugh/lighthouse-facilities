package gov.va.api.lighthouse.facilities.collector;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.List;
import org.junit.jupiter.api.Test;

public class BenefitsTransformerTest {

  @Test
  void benefitsServices() {

    // Note the BenefitsSamples class defines the valid facility services for this test. AND WE GET
    // THEM ALL!!!
    assertThat(tx().services())
        .isEqualTo(
            facilityService(
                List.of(
                    Facility.BenefitsService.ApplyingForBenefits,
                    Facility.BenefitsService.DisabilityClaimAssistance,
                    Facility.BenefitsService.eBenefitsRegistrationAssistance,
                    Facility.BenefitsService.EducationAndCareerCounseling,
                    Facility.BenefitsService.EducationClaimAssistance,
                    Facility.BenefitsService.FamilyMemberClaimAssistance,
                    Facility.BenefitsService.HomelessAssistance,
                    Facility.BenefitsService.VAHomeLoanAssistance,
                    Facility.BenefitsService.InsuranceClaimAssistanceAndFinancialCounseling,
                    Facility.BenefitsService.IntegratedDisabilityEvaluationSystemAssistance,
                    Facility.BenefitsService.PreDischargeClaimAssistance,
                    Facility.BenefitsService.TransitionAssistance,
                    Facility.BenefitsService.UpdatingDirectDepositInformation,
                    Facility.BenefitsService.VocationalRehabilitationAndEmploymentAssistance,
                    Facility.BenefitsService.Pensions)));
  }

  private Facility.Services facilityService(List<Facility.BenefitsService> services) {
    return Facility.Services.builder().benefits(services).build();
  }

  @Test
  void toFacility() {
    assertThat(tx().toFacility())
        .isEqualTo(BenefitsSamples.Facilities.create().benefitsFacilities().get(0));
  }

  @Test
  void transformerPrioritizesWebsiteFromCdw() {
    String cdw = "https://shanktopus.com/vha/facility";
    String csv = "https://shanktofake.com/nope";
    assertThat(tx().website(null)).isNull();
    assertThat(tx(csv).website(cdw)).isEqualTo(cdw);
  }

  private BenefitsTransformer tx() {
    return tx(null);
  }

  private BenefitsTransformer tx(String csvWebsite) {
    return BenefitsTransformer.builder()
        .cdwFacility(BenefitsSamples.Cdw.create().cdwBenefits())
        .csvWebsite(csvWebsite)
        .timeZoneMap(CalculateTimeZone.getEverywhereTimeZoneMap())
        .build();
  }

  @Test
  void websiteInCsvReturnsValueWhenCdwIsNull() {
    String url = "https://shanktopus.com/vha/facility";
    assertThat(tx(url).toFacility().attributes().website()).isEqualTo(url);
  }
}
