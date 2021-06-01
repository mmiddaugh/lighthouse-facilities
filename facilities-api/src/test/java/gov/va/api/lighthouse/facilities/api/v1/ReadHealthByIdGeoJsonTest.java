package gov.va.api.lighthouse.facilities.api.v1;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class ReadHealthByIdGeoJsonTest {
  @SneakyThrows
  private void assertReadable(String json) {
    gov.va.api.lighthouse.facilities.api.v1.GeoFacility f =
        createMapper()
            .readValue(
                getClass().getResourceAsStream(json),
                gov.va.api.lighthouse.facilities.api.v1.GeoFacility.class);
    assertThat(f).isEqualTo(sample());
  }

  private gov.va.api.lighthouse.facilities.api.v1.Facility.PatientWaitTime patientWaitTime(
      gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService service,
      Double newPat,
      Double oldPat) {
    gov.va.api.lighthouse.facilities.api.v1.Facility.PatientWaitTime.PatientWaitTimeBuilder
        waitTime = gov.va.api.lighthouse.facilities.api.v1.Facility.PatientWaitTime.builder();
    if (service != null) {
      waitTime.service(service);
    }
    if (newPat != null) {
      waitTime.newPatientWaitTime(BigDecimal.valueOf(newPat));
    }
    if (oldPat != null) {
      waitTime.establishedPatientWaitTime(BigDecimal.valueOf(oldPat));
    }
    return waitTime.build();
  }

  private gov.va.api.lighthouse.facilities.api.v1.GeoFacility sample() {
    return gov.va.api.lighthouse.facilities.api.v1.GeoFacility.builder()
        .type(gov.va.api.lighthouse.facilities.api.v1.GeoFacility.Type.Feature)
        .geometry(
            gov.va.api.lighthouse.facilities.api.v1.GeoFacility.Geometry.builder()
                .type(gov.va.api.lighthouse.facilities.api.v1.GeoFacility.GeometryType.Point)
                .coordinates(
                    List.of(BigDecimal.valueOf(-68.00939541), BigDecimal.valueOf(46.8780264900001)))
                .build())
        .properties(
            GeoFacility.Properties.builder()
                .id("vha_402GA")
                .name("Caribou VA Clinic")
                .facilityType(
                    gov.va.api.lighthouse.facilities.api.v1.Facility.FacilityType
                        .va_health_facility)
                .classification("Primary Care CBOC")
                .website("https://www.maine.va.gov/locations/caribou.asp")
                .address(
                    gov.va.api.lighthouse.facilities.api.v1.Facility.Addresses.builder()
                        .mailing(
                            gov.va.api.lighthouse.facilities.api.v1.Facility.Address.builder()
                                .build())
                        .physical(
                            gov.va.api.lighthouse.facilities.api.v1.Facility.Address.builder()
                                .zip("04736-3567")
                                .city("Caribou")
                                .state("ME")
                                .address1("163 Van Buren Road")
                                .address3("Suite 6")
                                .build())
                        .build())
                .phone(
                    gov.va.api.lighthouse.facilities.api.v1.Facility.Phone.builder()
                        .fax("207-493-3877")
                        .main("207-493-3800")
                        .pharmacy("207-623-8411 x5770")
                        .afterHours("844-750-8426")
                        .patientAdvocate("207-623-5760")
                        .mentalHealthClinic("207-623-8411 x 7490")
                        .enrollmentCoordinator("207-623-8411 x5688")
                        .build())
                .hours(
                    gov.va.api.lighthouse.facilities.api.v1.Facility.Hours.builder()
                        .mon("700AM-430PM")
                        .tues("700AM-430PM")
                        .wed("700AM-430PM")
                        .thurs("700AM-430PM")
                        .fri("700AM-430PM")
                        .sat("Closed")
                        .sun("Closed")
                        .build())
                .services(
                    gov.va.api.lighthouse.facilities.api.v1.Facility.Services.builder()
                        .other(new ArrayList<>())
                        .health(
                            List.of(
                                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService
                                    .EmergencyCare,
                                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService
                                    .PrimaryCare,
                                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService
                                    .MentalHealthCare,
                                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService
                                    .Dermatology,
                                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService
                                    .SpecialtyCare))
                        .lastUpdated(LocalDate.parse("2020-02-24"))
                        .build())
                .satisfaction(
                    gov.va.api.lighthouse.facilities.api.v1.Facility.Satisfaction.builder()
                        .health(
                            gov.va.api.lighthouse.facilities.api.v1.Facility.PatientSatisfaction
                                .builder()
                                .primaryCareUrgent(BigDecimal.valueOf(0.89))
                                .primaryCareRoutine(BigDecimal.valueOf(0.91))
                                .build())
                        .effectiveDate(LocalDate.parse("2019-06-20"))
                        .build())
                .waitTimes(
                    gov.va.api.lighthouse.facilities.api.v1.Facility.WaitTimes.builder()
                        .health(
                            List.of(
                                patientWaitTime(
                                    gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService
                                        .Dermatology,
                                    3.714285,
                                    null),
                                patientWaitTime(
                                    gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService
                                        .PrimaryCare,
                                    13.727272,
                                    10.392441),
                                patientWaitTime(
                                    gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService
                                        .SpecialtyCare,
                                    5.222222,
                                    0.0),
                                patientWaitTime(
                                    gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService
                                        .MentalHealthCare,
                                    5.75,
                                    2.634703)))
                        .effectiveDate(LocalDate.parse("2020-02-24"))
                        .build())
                .mobile(false)
                .activeStatus(Facility.ActiveStatus.A)
                .visn("1")
                .build())
        .build();
  }

  @Test
  void unmarshallSample() {
    assertReadable("/read-health-geojson.json");
  }
}
