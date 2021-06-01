package gov.va.api.lighthouse.facilities.api.v1;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v0.PageLinks;
import gov.va.api.lighthouse.facilities.api.v0.Pagination;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class SearchByZipJsonTest {
  @SneakyThrows
  private void assertReadable(String json) {
    FacilitiesResponse f =
        createMapper().readValue(getClass().getResourceAsStream(json), FacilitiesResponse.class);
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

  private FacilitiesResponse sample() {
    return FacilitiesResponse.builder()
        .links(
            PageLinks.builder()
                .self("https://dev-api.vets.gov/services/va_facilities/v1/facilities?zip=32940")
                .first(
                    "https://dev-api.vets.gov/services/va_facilities/v1/facilities?page=1&per_page=10&zip=32940")
                .last(
                    "https://dev-api.vets.gov/services/va_facilities/v1/facilities?page=1&per_page=10&zip=32940")
                .build())
        .meta(
            FacilitiesResponse.FacilitiesMetadata.builder()
                .distances(new ArrayList<>())
                .pagination(
                    Pagination.builder()
                        .currentPage(1)
                        .entriesPerPage(10)
                        .totalEntries(1)
                        .totalPages(1)
                        .build())
                .build())
        .data(
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.builder()
                    .id("vha_675GA")
                    .type(gov.va.api.lighthouse.facilities.api.v1.Facility.Type.va_facilities)
                    .attributes(
                        gov.va.api.lighthouse.facilities.api.v1.Facility.FacilityAttributes
                            .builder()
                            .name("Viera VA Clinic")
                            .facilityType(
                                gov.va.api.lighthouse.facilities.api.v1.Facility.FacilityType
                                    .va_health_facility)
                            .classification("Health Care Center (HCC)")
                            .website("https://www.orlando.va.gov/locations/Viera.asp")
                            .latitude(BigDecimal.valueOf(28.2552385700001))
                            .longitude(BigDecimal.valueOf(-80.73907113))
                            .address(
                                gov.va.api.lighthouse.facilities.api.v1.Facility.Addresses.builder()
                                    .mailing(
                                        gov.va.api.lighthouse.facilities.api.v1.Facility.Address
                                            .builder()
                                            .build())
                                    .physical(
                                        gov.va.api.lighthouse.facilities.api.v1.Facility.Address
                                            .builder()
                                            .zip("32940-8007")
                                            .city("Viera")
                                            .state("FL")
                                            .address1("2900 Veterans Way")
                                            .build())
                                    .build())
                            .phone(
                                gov.va.api.lighthouse.facilities.api.v1.Facility.Phone.builder()
                                    .fax("321-637-3515")
                                    .main("321-637-3788")
                                    .pharmacy("877-646-4550")
                                    .afterHours("877-741-3400")
                                    .patientAdvocate("407-631-1187")
                                    .mentalHealthClinic("321-637-3788")
                                    .enrollmentCoordinator("321-637-3527")
                                    .build())
                            .hours(
                                gov.va.api.lighthouse.facilities.api.v1.Facility.Hours.builder()
                                    .monday("730AM-430PM")
                                    .tuesday("730AM-430PM")
                                    .wednesday("730AM-430PM")
                                    .thursday("730AM-430PM")
                                    .friday("730AM-430PM")
                                    .saturday("Closed")
                                    .sunday("Closed")
                                    .build())
                            .services(
                                gov.va.api.lighthouse.facilities.api.v1.Facility.Services.builder()
                                    .other(new ArrayList<>())
                                    .health(
                                        List.of(
                                            gov.va.api.lighthouse.facilities.api.v1.Facility
                                                .HealthService.PrimaryCare,
                                            gov.va.api.lighthouse.facilities.api.v1.Facility
                                                .HealthService.MentalHealthCare,
                                            gov.va.api.lighthouse.facilities.api.v1.Facility
                                                .HealthService.Audiology,
                                            gov.va.api.lighthouse.facilities.api.v1.Facility
                                                .HealthService.Cardiology,
                                            gov.va.api.lighthouse.facilities.api.v1.Facility
                                                .HealthService.Dermatology,
                                            gov.va.api.lighthouse.facilities.api.v1.Facility
                                                .HealthService.Gastroenterology,
                                            gov.va.api.lighthouse.facilities.api.v1.Facility
                                                .HealthService.Ophthalmology,
                                            gov.va.api.lighthouse.facilities.api.v1.Facility
                                                .HealthService.Optometry,
                                            gov.va.api.lighthouse.facilities.api.v1.Facility
                                                .HealthService.Orthopedics,
                                            gov.va.api.lighthouse.facilities.api.v1.Facility
                                                .HealthService.Urology,
                                            gov.va.api.lighthouse.facilities.api.v1.Facility
                                                .HealthService.SpecialtyCare,
                                            gov.va.api.lighthouse.facilities.api.v1.Facility
                                                .HealthService.DentalServices))
                                    .lastUpdated(LocalDate.parse("2020-03-02"))
                                    .build())
                            .satisfaction(
                                gov.va.api.lighthouse.facilities.api.v1.Facility.Satisfaction
                                    .builder()
                                    .health(
                                        gov.va.api.lighthouse.facilities.api.v1.Facility
                                            .PatientSatisfaction.builder()
                                            .primaryCareUrgent(BigDecimal.valueOf(0.74))
                                            .primaryCareRoutine(BigDecimal.valueOf(0.83))
                                            .build())
                                    .effectiveDate(LocalDate.parse("2019-06-20"))
                                    .build())
                            .waitTimes(
                                gov.va.api.lighthouse.facilities.api.v1.Facility.WaitTimes.builder()
                                    .health(
                                        List.of(
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v1.Facility
                                                    .HealthService.Urology,
                                                32.047619,
                                                9.879032),
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v1.Facility
                                                    .HealthService.Audiology,
                                                1.706967,
                                                2.126855),
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v1.Facility
                                                    .HealthService.Optometry,
                                                76.396226,
                                                7.900787),
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v1.Facility
                                                    .HealthService.Cardiology,
                                                18.657142,
                                                6.4),
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v1.Facility
                                                    .HealthService.Dermatology,
                                                0.616666,
                                                0.555555),
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v1.Facility
                                                    .HealthService.Orthopedics,
                                                24.682539,
                                                4.995024),
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v1.Facility
                                                    .HealthService.PrimaryCare,
                                                26.405405,
                                                1.545372),
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v1.Facility
                                                    .HealthService.Ophthalmology,
                                                47.571428,
                                                3.258992),
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v1.Facility
                                                    .HealthService.SpecialtyCare,
                                                20.963572,
                                                5.775406),
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v1.Facility
                                                    .HealthService.Gastroenterology,
                                                22.151515,
                                                4.943661),
                                            patientWaitTime(
                                                gov.va.api.lighthouse.facilities.api.v1.Facility
                                                    .HealthService.MentalHealthCare,
                                                7.592814,
                                                3.97159)))
                                    .effectiveDate(LocalDate.parse("2020-03-02"))
                                    .build())
                            .mobile(false)
                            .activeStatus(Facility.ActiveStatus.A)
                            .visn("8")
                            .build())
                    .build()))
        .build();
  }

  @Test
  void unmarshallSample() {
    assertReadable("/v1/search-zip.json");
  }
}
