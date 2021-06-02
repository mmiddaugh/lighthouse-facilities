package gov.va.api.lighthouse.facilities.v0;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.FacilitiesJacksonConfig;
import gov.va.api.lighthouse.facilities.api.v0.FacilityV0;
import gov.va.api.lighthouse.facilities.api.v0.FacilityV0.ActiveStatus;
import gov.va.api.lighthouse.facilities.api.v0.FacilityV0.FacilityAttributes;
import gov.va.api.lighthouse.facilities.api.v0.FacilityV0.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.v0.FacilityV0.OperatingStatusCode;
import gov.va.api.lighthouse.facilities.api.v0.cms.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v0.cms.DetailedService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class FacilityOverlayV0Test {
  private static final ObjectMapper mapper = FacilitiesJacksonConfig.createMapper();

  @Test
  void activeStatusIsPopulatedByOperatingStatusWhenAvailable() {
    assertStatus(
        ActiveStatus.A,
        op(OperatingStatusCode.NORMAL, "neato"),
        null,
        entity(
            fromActiveStatus(ActiveStatus.T),
            overlay(op(OperatingStatusCode.NORMAL, "neato"), false)));
    assertStatus(
        ActiveStatus.A,
        op(OperatingStatusCode.NOTICE, "neato"),
        null,
        entity(
            fromActiveStatus(ActiveStatus.T),
            overlay(op(OperatingStatusCode.NOTICE, "neato"), false)));
    assertStatus(
        ActiveStatus.A,
        op(OperatingStatusCode.LIMITED, "neato"),
        List.of(FacilityV0.HealthService.Covid19Vaccine),
        entity(
            fromActiveStatus(ActiveStatus.T),
            overlay(op(OperatingStatusCode.LIMITED, "neato"), true)));
    assertStatus(
        ActiveStatus.T,
        op(OperatingStatusCode.CLOSED, "neato"),
        List.of(FacilityV0.HealthService.Covid19Vaccine),
        entity(
            fromActiveStatus(ActiveStatus.A),
            overlay(op(OperatingStatusCode.CLOSED, "neato"), true)));
  }

  private void assertStatus(
      ActiveStatus expectedActiveStatus,
      OperatingStatus expectedOperatingStatus,
      List<FacilityV0.HealthService> expectedHealthServices,
      FacilityEntityV0 entity) {
    FacilityV0 facility = FacilityOverlayV0.builder().mapper(mapper).build().apply(entity);
    assertThat(facility.attributes().activeStatus()).isEqualTo(expectedActiveStatus);
    assertThat(facility.attributes().operatingStatus()).isEqualTo(expectedOperatingStatus);
    assertThat(facility.attributes().services().health()).isEqualTo(expectedHealthServices);
  }

  @Test
  void covid19VaccineIsPopulatedWhenAvailable() {
    assertStatus(
        null,
        OperatingStatus.builder().code(OperatingStatusCode.NORMAL).build(),
        List.of(FacilityV0.HealthService.Covid19Vaccine),
        entity(fromActiveStatus(null), overlay(null, true)));
    assertStatus(
        null,
        OperatingStatus.builder().code(OperatingStatusCode.NORMAL).build(),
        null,
        entity(fromActiveStatus(null), overlay(null, false)));
  }

  private DetailedService createDetailedService(boolean cmsServiceActiveValue) {
    return DetailedService.builder()
        .name("Covid19Vaccine")
        .active(cmsServiceActiveValue)
        .changed("2021-02-04T22:36:49+00:00")
        .descriptionFacility("Facility description for vaccine availability for COVID-19")
        .appointmentLeadIn("Your VA health care team will contact you if you...more text")
        .onlineSchedulingAvailable("True")
        .path("\\/erie-health-care\\/locations\\/erie-va-medical-center\\/covid-19-vaccines")
        .phoneNumbers(
            List.of(
                DetailedService.AppointmentPhoneNumber.builder()
                    .extension("123")
                    .label("Main phone")
                    .number("555-555-1212")
                    .type("tel")
                    .build()))
        .referralRequired("True")
        .walkInsAccepted("False")
        .serviceLocations(
            List.of(
                DetailedService.DetailedServiceLocation.builder()
                    .serviceLocationAddress(
                        DetailedService.DetailedServiceAddress.builder()
                            .buildingNameNumber("Baxter Building")
                            .clinicName("Baxter Clinic")
                            .wingFloorOrRoomNumber("Wing East")
                            .address1("122 Main St.")
                            .address2(null)
                            .city("Rochester")
                            .state("NY")
                            .zipCode("14623-1345")
                            .countryCode("US")
                            .build())
                    .appointmentPhoneNumbers(
                        List.of(
                            DetailedService.AppointmentPhoneNumber.builder()
                                .extension("567")
                                .label("Alt phone")
                                .number("556-565-1119")
                                .type("tel")
                                .build()))
                    .emailContacts(
                        List.of(
                            DetailedService.DetailedServiceEmailContact.builder()
                                .emailAddress("georgea@va.gov")
                                .emailLabel("George Anderson")
                                .build()))
                    .facilityServiceHours(
                        DetailedService.DetailedServiceHours.builder()
                            .monday("8:30AM-7:00PM")
                            .tuesday("8:30AM-7:00PM")
                            .wednesday("8:30AM-7:00PM")
                            .thursday("8:30AM-7:00PM")
                            .friday("8:30AM-7:00PM")
                            .saturday("8:30AM-7:00PM")
                            .sunday("CLOSED")
                            .build())
                    .additionalHoursInfo("Please call for an appointment outside...")
                    .build()))
        .build();
  }

  @SneakyThrows
  private FacilityEntityV0 entity(FacilityV0 facility, CmsOverlay overlay) {
    Set<String> detailedServices = null;
    if (overlay != null) {
      detailedServices = new HashSet<>();
      for (DetailedService service : overlay.detailedServices()) {
        if (service.active()) {
          detailedServices.add(service.name());
        }
      }
    }
    return FacilityEntityV0.builder()
        .facility(mapper.writeValueAsString(facility))
        .cmsOperatingStatus(
            overlay == null ? null : mapper.writeValueAsString(overlay.operatingStatus()))
        .overlayServices(overlay == null ? null : detailedServices)
        .cmsServices(overlay == null ? null : mapper.writeValueAsString(overlay.detailedServices()))
        .build();
  }

  private FacilityV0 fromActiveStatus(ActiveStatus status) {
    return FacilityV0.builder()
        .attributes(FacilityAttributes.builder().activeStatus(status).build())
        .build();
  }

  private OperatingStatus op(OperatingStatusCode code, String info) {
    return OperatingStatus.builder().code(code).additionalInfo(info).build();
  }

  @Test
  void operatingStatusIsPopulatedByActiveStatusWhenNotAvailable() {
    assertStatus(
        ActiveStatus.A,
        OperatingStatus.builder().code(OperatingStatusCode.NORMAL).build(),
        null,
        entity(fromActiveStatus(ActiveStatus.A), null));
    assertStatus(
        ActiveStatus.T,
        OperatingStatus.builder().code(OperatingStatusCode.CLOSED).build(),
        null,
        entity(fromActiveStatus(ActiveStatus.T), null));
    assertStatus(
        null,
        OperatingStatus.builder().code(OperatingStatusCode.NORMAL).build(),
        null,
        entity(fromActiveStatus(null), null));
  }

  private CmsOverlay overlay(OperatingStatus neato, boolean cmsServiceActiveValue) {
    return CmsOverlay.builder()
        .operatingStatus(neato)
        .detailedServices(List.of(createDetailedService(cmsServiceActiveValue)))
        .build();
  }
}
