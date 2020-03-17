package gov.va.api.lighthouse.facilities.api.facilities;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.Test;

public class ModelTest {
  private Facility.Addresses addresses() {
    return Facility.Addresses.builder()
        .mailing(
            Facility.Address.builder()
                .address1("1")
                .address2("Two")
                .address3("Drive")
                .city("Melbourne")
                .state("FL")
                .zip("32935")
                .build())
        .build();
  }

  @Test
  public void apiError() {
    roundTrip(
        ApiError.builder()
            .errors(
                List.of(
                    ApiError.ErrorMessage.builder()
                        .title("Hello")
                        .detail("Its Me.")
                        .code("43110")
                        .status("Error")
                        .build()))
            .build());
  }

  @Test
  public void facilitiesReadResponse() {
    roundTrip(FacilitiesReadResponse.builder().data(facility()).build());
  }

  @Test
  public void facilitiesSearchResponse() {
    roundTrip(
        FacilitiesSearchResponse.builder()
            .meta(
                FacilitiesSearchResponse.Metadata.builder()
                    .distances(
                        List.of(
                            FacilitiesSearchResponse.Distance.builder()
                                .id("BigBoi")
                                .distance(BigDecimal.valueOf(120.95))
                                .build()))
                    .build())
            .data(List.of(facility()))
            .build());
  }

  private Facility facility() {
    return Facility.builder()
        .id("98")
        .type(Facility.Type.va_facilities)
        .attributes(
            Facility.Attributes.builder()
                .name("Shanktopod")
                .facilityType(Facility.FacilityType.va_benefits_facility)
                .classification("VA Medical Center (VAMC)")
                .latitude(BigDecimal.valueOf(38.9311137))
                .longitude(BigDecimal.valueOf(-77.0109110499999))
                .website("http://www.washingtondc.va.gov/")
                .address(addresses())
                .phone(phones())
                .hours(hours())
                .services(services())
                .satisfaction(satisfaction())
                .waitTimes(waitTimes())
                .mobile(false)
                .activeStatus(Facility.ActiveStatus.T)
                .visn("20")
                .build())
        .build();
  }

  @Test
  public void genericError() {
    roundTrip(GenericError.builder().message("First Try Baby").build());
  }

  @Test
  public void geoFacilitiesResponse() {
    roundTrip(
        GeoFacilitiesResponse.builder()
            .type(GeoFacilitiesResponse.Type.FeatureCollection)
            .features(List.of(geofacility()))
            .build());
  }

  @Test
  public void geoFacilityRoundTrip() {
    roundTrip(geofacility());
  }

  public GeoFacility geofacility() {
    return GeoFacility.builder()
        .type(GeoFacility.Type.Feature)
        .geometry(
            GeoFacility.Geometry.builder()
                .type(GeoFacility.GeometryType.Point)
                .coordinates(
                    List.of(BigDecimal.valueOf(-77.0367761), BigDecimal.valueOf(38.9004181)))
                .build())
        .properties(
            GeoFacility.Properties.builder()
                .id("1234")
                .name("Cutsortium")
                .facilityType(Facility.FacilityType.va_health_facility)
                .classification("VA Medical Center (VAMC)")
                .website("http://www.washingtondc.va.gov")
                .address(addresses())
                .phone(phones())
                .hours(hours())
                .services(services())
                .satisfaction(satisfaction())
                .waitTimes(waitTimes())
                .mobile(true)
                .activeStatus(Facility.ActiveStatus.A)
                .visn("20")
                .build())
        .build();
  }

  private Facility.Hours hours() {
    return Facility.Hours.builder()
        .sunday("CLOSED")
        .monday("CLOSED")
        .tuesday("CLOSED")
        .wednesday("CLOSED")
        .thursday("CLOSED")
        .friday("CLOSED")
        .saturday("CLOSED")
        .build();
  }

  @Test
  public void nearbyFacility() {
    roundTrip(
        NearbyFacility.builder()
            .data(
                List.of(
                    NearbyFacility.Nearby.builder()
                        .id("8")
                        .type(NearbyFacility.Type.NearbyFacility)
                        .attributes(
                            NearbyFacility.Attributes.builder().minTime(10).maxTime(20).build())
                        .relationships(
                            NearbyFacility.Relationships.builder()
                                .vaFacility(
                                    NearbyFacility.VaFacility.builder()
                                        .links(
                                            NearbyFacility.Links.builder()
                                                .related(
                                                    "/services/va_facilities/v0/facilities/vha_688")
                                                .build())
                                        .build())
                                .build())
                        .build()))
            .build());
  }

  private Facility.Phone phones() {
    return Facility.Phone.builder()
        .patientAdvocate("123-456-7989")
        .mentalHealthClinic("7412589630")
        .enrollmentCoordinator("1594782360")
        .main("(123)456-7890")
        .fax("(456)678-1230")
        .pharmacy("789-456-1230")
        .afterHours("(123)456-7890")
        .build();
  }

  @SneakyThrows
  private <T> void roundTrip(T object) {
    ObjectMapper mapper = new JacksonConfig().objectMapper();
    String json = mapper.writeValueAsString(object);
    Object evilTwin = mapper.readValue(json, object.getClass());
    assertThat(evilTwin).isEqualTo(object);
  }

  private Facility.Satisfaction satisfaction() {
    return Facility.Satisfaction.builder()
        .health(
            Facility.PatientSatisfaction.builder()
                .primaryCareUrgent(BigDecimal.valueOf(8.0))
                .specialtyCareRoutine(BigDecimal.valueOf(8.0))
                .specialtyCareUrgent(BigDecimal.valueOf(8.0))
                .primaryCareRoutine(BigDecimal.valueOf(8.0))
                .build())
        .effectiveDate(LocalDate.parse("2020-01-20"))
        .build();
  }

  private Facility.Services services() {
    return Facility.Services.builder()
        .benefits(List.of(Facility.BenefitsService.eBenefitsRegistrationAssistance))
        .lastUpdated(LocalDate.parse("2020-03-12"))
        .build();
  }

  private Facility.WaitTimes waitTimes() {
    return Facility.WaitTimes.builder()
        .health(
            List.of(
                Facility.PatientWaitTime.builder()
                    .newPatientWaitTime(BigDecimal.valueOf(25))
                    .establishedPatientWaitTime(BigDecimal.valueOf(10))
                    .service(Facility.HealthService.Audiology)
                    .build()))
        .effectiveDate(LocalDate.parse("2020-03-12"))
        .build();
  }
}