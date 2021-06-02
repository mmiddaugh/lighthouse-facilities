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

  private Facility.PatientWaitTime patientWaitTime(
      Facility.HealthService service, Double newPat, Double oldPat) {
    Facility.PatientWaitTime.PatientWaitTimeBuilder waitTime = Facility.PatientWaitTime.builder();
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
                .facilityType(Facility.FacilityType.va_health_facility)
                .classification("Primary Care CBOC")
                .website("https://www.maine.va.gov/locations/caribou.asp")
                .address(
                    Facility.Addresses.builder()
                        .mailing(Facility.Address.builder().build())
                        .physical(
                            Facility.Address.builder()
                                .zip("04736-3567")
                                .city("Caribou")
                                .state("ME")
                                .address1("163 Van Buren Road")
                                .address3("Suite 6")
                                .build())
                        .build())
                .phone(
                    Facility.Phone.builder()
                        .fax("207-493-3877")
                        .main("207-493-3800")
                        .pharmacy("207-623-8411 x5770")
                        .afterHours("844-750-8426")
                        .patientAdvocate("207-623-5760")
                        .mentalHealthClinic("207-623-8411 x 7490")
                        .enrollmentCoordinator("207-623-8411 x5688")
                        .build())
                .hours(
                    Facility.Hours.builder()
                        .mon("700AM-430PM")
                        .tues("700AM-430PM")
                        .wed("700AM-430PM")
                        .thurs("700AM-430PM")
                        .fri("700AM-430PM")
                        .sat("Closed")
                        .sun("Closed")
                        .build())
                .services(
                    Facility.Services.builder()
                        .other(new ArrayList<>())
                        .health(
                            List.of(
                                Facility.HealthService.EmergencyCare,
                                Facility.HealthService.PrimaryCare,
                                Facility.HealthService.MentalHealthCare,
                                Facility.HealthService.Dermatology,
                                Facility.HealthService.SpecialtyCare))
                        .lastUpdated(LocalDate.parse("2020-02-24"))
                        .build())
                .satisfaction(
                    Facility.Satisfaction.builder()
                        .health(
                            Facility.PatientSatisfaction.builder()
                                .primaryCareUrgent(BigDecimal.valueOf(0.89))
                                .primaryCareRoutine(BigDecimal.valueOf(0.91))
                                .build())
                        .effectiveDate(LocalDate.parse("2019-06-20"))
                        .build())
                .waitTimes(
                    Facility.WaitTimes.builder()
                        .health(
                            List.of(
                                patientWaitTime(Facility.HealthService.Dermatology, 3.714285, null),
                                patientWaitTime(
                                    Facility.HealthService.PrimaryCare, 13.727272, 10.392441),
                                patientWaitTime(
                                    Facility.HealthService.SpecialtyCare, 5.222222, 0.0),
                                patientWaitTime(
                                    Facility.HealthService.MentalHealthCare, 5.75, 2.634703)))
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
