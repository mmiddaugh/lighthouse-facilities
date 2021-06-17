package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.collector.Transformers.allBlank;
import static gov.va.api.lighthouse.facilities.collector.Transformers.checkAngleBracketNull;
import static gov.va.api.lighthouse.facilities.collector.Transformers.phoneTrim;
import static org.apache.commons.lang3.StringUtils.upperCase;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import us.dustinj.timezonemap.TimeZoneMap;

@Builder
@SuppressWarnings("ObjectToString")
final class BenefitsTransformer {
  @NonNull private final TimeZoneMap timeZoneMap;

  @NonNull CdwBenefits cdwFacility;

  String csvWebsite;

  private Facility.FacilityAttributes attributes() {
    return Facility.FacilityAttributes.builder()
        .name(cdwFacility.facilityName())
        .facilityType(Facility.FacilityType.va_benefits_facility)
        .classification(cdwFacility.facilityType())
        .latitude(cdwFacility.latitude())
        .longitude(cdwFacility.longitude())
        .timeZone(
            CalculateTimeZone.calculateTimeZonesWithMap(
                cdwFacility.latitude(), cdwFacility.longitude(), timeZoneMap))
        .website(website(cdwFacility.websiteUrl()))
        .address(
            Facility.Addresses.builder()
                .physical(
                    Facility.Address.builder()
                        .address1(checkAngleBracketNull(cdwFacility.address1()))
                        .address2(checkAngleBracketNull(cdwFacility.address2()))
                        .city(cdwFacility.city())
                        .state(upperCase(cdwFacility.state(), Locale.US))
                        .zip(cdwFacility.zip())
                        .build())
                .build())
        .phone(phone())
        .hours(
            Facility.Hours.builder()
                .monday(cdwFacility.monday())
                .tuesday(cdwFacility.tuesday())
                .wednesday(cdwFacility.wednesday())
                .thursday(cdwFacility.thursday())
                .friday(cdwFacility.friday())
                .saturday(cdwFacility.saturday())
                .sunday(cdwFacility.sunday())
                .build())
        .services(services())
        .build();
  }

  private Facility.Phone phone() {
    String main = phoneTrim(cdwFacility.phone());
    String fax = phoneTrim(cdwFacility.fax());
    if (allBlank(main, fax)) {
      return null;
    } else {
      return Facility.Phone.builder().main(main).fax(fax).build();
    }
  }

  Facility.Services services() {
    List<Facility.BenefitsService> benefitsServices = new ArrayList<>();
    if (yesNoToBoolean(cdwFacility.applyingForBenefits())) {
      benefitsServices.add(Facility.BenefitsService.ApplyingForBenefits);
    }
    if (yesNoToBoolean(cdwFacility.burialClaimAssistance())) {
      benefitsServices.add(Facility.BenefitsService.BurialClaimAssistance);
    }
    if (yesNoToBoolean(cdwFacility.disabilityClaimAssistance())) {
      benefitsServices.add(Facility.BenefitsService.DisabilityClaimAssistance);
    }
    if (yesNoToBoolean(cdwFacility.ebenefitsRegistration())) {
      benefitsServices.add(Facility.BenefitsService.eBenefitsRegistrationAssistance);
    }
    if (yesNoToBoolean(cdwFacility.educationAndCareerCounseling())) {
      benefitsServices.add(Facility.BenefitsService.EducationAndCareerCounseling);
    }
    if (yesNoToBoolean(cdwFacility.educationClaimAssistance())) {
      benefitsServices.add(Facility.BenefitsService.EducationClaimAssistance);
    }
    if (yesNoToBoolean(cdwFacility.familyMemberClaimAssistance())) {
      benefitsServices.add(Facility.BenefitsService.FamilyMemberClaimAssistance);
    }
    if (yesNoToBoolean(cdwFacility.homelessAssistance())) {
      benefitsServices.add(Facility.BenefitsService.HomelessAssistance);
    }
    if (yesNoToBoolean(cdwFacility.vaHomeLoanAssistance())) {
      benefitsServices.add(Facility.BenefitsService.VAHomeLoanAssistance);
    }
    if (yesNoToBoolean(cdwFacility.insuranceClaimAssistance())) {
      benefitsServices.add(Facility.BenefitsService.InsuranceClaimAssistanceAndFinancialCounseling);
    }
    if (yesNoToBoolean(cdwFacility.integratedDisabilityEvaluationSystem())) {
      benefitsServices.add(Facility.BenefitsService.IntegratedDisabilityEvaluationSystemAssistance);
    }
    if (yesNoToBoolean(cdwFacility.preDischargeClaimAssistance())) {
      benefitsServices.add(Facility.BenefitsService.PreDischargeClaimAssistance);
    }
    if (yesNoToBoolean(cdwFacility.transitionAssistance())) {
      benefitsServices.add(Facility.BenefitsService.TransitionAssistance);
    }
    if (yesNoToBoolean(cdwFacility.updatingDirectDepositInformation())) {
      benefitsServices.add(Facility.BenefitsService.UpdatingDirectDepositInformation);
    }
    if (yesNoToBoolean(cdwFacility.vocationalRehabilitationEmplo())) {
      benefitsServices.add(
          Facility.BenefitsService.VocationalRehabilitationAndEmploymentAssistance);
    }
    if (StringUtils.containsIgnoreCase(cdwFacility.otherServices(), "PENSION")) {
      benefitsServices.add(Facility.BenefitsService.Pensions);
    }
    return Facility.Services.builder().benefits(benefitsServices).build();
  }

  Facility toFacility() {
    return Facility.builder()
        .id("vba_" + cdwFacility.facilityNumber())
        .type(Facility.Type.va_facilities)
        .attributes(attributes())
        .build();
  }

  String website(String website) {
    /* CDW returns a string NULL... We don't want to return that.*/
    return website == null || website.equalsIgnoreCase("NULL") ? csvWebsite : website;
  }

  private boolean yesNoToBoolean(String yesNo) {
    /* Assume no if the answer is not an emphatic yes or the string is null. */
    return "YES".equalsIgnoreCase(yesNo);
  }
}
