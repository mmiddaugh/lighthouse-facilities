package gov.va.api.lighthouse.facilities.collector;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
class BenefitsSamples {

  @AllArgsConstructor(staticName = "create")
  static final class Cdw {
    CdwBenefits cdwBenefits() {
      return CdwBenefits.builder()
          .facilityName("Shanktopus VAMC")
          .facilityNumber("306e")
          .facilityType("VAMC")
          .address1("8 Shanktopus Lane")
          .address2("Apartment 8")
          .city("North")
          .state("sd")
          .zip("12208")
          .fax("123-456-7890")
          .phone("123-789-0456")
          .monday("8AM-8PM")
          .tuesday("8AM-8PM")
          .wednesday("8AM-8PM")
          .thursday("8AM-8PM")
          .friday("8AM-8PM")
          .saturday("Closed")
          .sunday("Closed")
          .applyingForBenefits("YES")
          .disabilityClaimAssistance("YES")
          .ebenefitsRegistration("YES")
          .educationAndCareerCounseling("YES")
          .educationClaimAssistance("YES")
          .familyMemberClaimAssistance("YES")
          .homelessAssistance("YES")
          .vaHomeLoanAssistance("YES")
          .insuranceClaimAssistance("YES")
          .integratedDisabilityEvaluationSystem("YES")
          .preDischargeClaimAssistance("YES")
          .transitionAssistance("YES")
          .updatingDirectDepositInformation("YES")
          .vocationalRehabilitationEmplo("YES")
          .otherServices("We got pensions for days.")
          .websiteUrl("NULL")
          .latitude(new BigDecimal("-73.776232849999985"))
          .longitude(new BigDecimal("42.651408840000045"))
          .build();
    }
  }

  @AllArgsConstructor(staticName = "create")
  static final class Facilities {
    private Facility.Address address() {
      return Facility.Address.builder()
          .address1("8 Shanktopus Lane")
          .address2("Apartment 8")
          .city("North")
          .state("SD")
          .zip("12208")
          .build();
    }

    private Facility.FacilityAttributes attributes() {
      return Facility.FacilityAttributes.builder()
          .name("Shanktopus VAMC")
          .facilityType(Facility.FacilityType.va_benefits_facility)
          .classification("VAMC")
          .latitude(new BigDecimal("-73.776232849999985"))
          .longitude(new BigDecimal("42.651408840000045"))
          .timeZone("Antarctica/Syowa")
          .address(Facility.Addresses.builder().physical(address()).build())
          .phone(Facility.Phone.builder().main("123-789-0456").fax("123-456-7890").build())
          .hours(
              Facility.Hours.builder()
                  .monday("8AM-8PM")
                  .tuesday("8AM-8PM")
                  .wednesday("8AM-8PM")
                  .thursday("8AM-8PM")
                  .friday("8AM-8PM")
                  .saturday("Closed")
                  .sunday("Closed")
                  .build())
          .services(
              Facility.Services.builder()
                  .benefits(
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
                          Facility.BenefitsService.Pensions))
                  .build())
          .build();
    }

    List<Facility> benefitsFacilities() {
      return List.of(
          Facility.builder()
              .id("vba_306e")
              .type(Facility.Type.va_facilities)
              .attributes(attributes())
              .build());
    }
  }
}
