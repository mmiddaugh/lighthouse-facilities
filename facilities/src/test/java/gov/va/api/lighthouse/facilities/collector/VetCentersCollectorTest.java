package gov.va.api.lighthouse.facilities.collector;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

public class VetCentersCollectorTest {
  @Test
  void collect() {
    VastEntity vastEntity =
        VastEntity.builder()
            .vetCenter(true)
            .latitude(new BigDecimal("48.20065600000004"))
            .longitude(new BigDecimal("-101.29615999999999"))
            .stationNumber("0404V")
            .stationName("Minot Vet Center")
            .abbreviation("VTCR")
            .cocClassificationId("X")
            .address1("Minot Vet Center")
            .address2("3300 South Broadway")
            .city("Minot")
            .state("ND")
            .staFax("771-025-8107")
            .staPhone("701-852-0177 x")
            .zip("58701")
            .zip4("9630")
            .monday("730AM-430PM")
            .tuesday("730AM-430PM")
            .wednesday("730AM-800PM")
            .thursday("730AM-430PM")
            .friday("730AM-430PM")
            .saturday("-")
            .sunday("-")
            .operationalHoursSpecialInstructions(
                "Administrative hours are Monday-Friday 8:00 a.m. to 4:30 p.m. |")
            .pod("A")
            .mobile(false)
            .visn("21")
            .build();

    assertThat(
            VetCentersCollector.builder()
                .vastEntities(List.of(vastEntity))
                .websites(emptyMap())
                .timeZoneMap(CalculateTimeZone.getEverywhereTimeZoneMap())
                .build()
                .collect())
        .isEqualTo(
            List.of(
                Facility.builder()
                    .id("vc_0404V")
                    .type(Facility.Type.va_facilities)
                    .attributes(
                        Facility.FacilityAttributes.builder()
                            .name("Minot Vet Center")
                            .facilityType(Facility.FacilityType.vet_center)
                            .latitude(new BigDecimal("48.20065600000004"))
                            .longitude(new BigDecimal("-101.29615999999999"))
                            .timeZone("America/Chicago")
                            .address(
                                Facility.Addresses.builder()
                                    .physical(
                                        Facility.Address.builder()
                                            .zip("58701-9630")
                                            .city("Minot")
                                            .state("ND")
                                            .address1("3300 South Broadway")
                                            .build())
                                    .build())
                            .phone(
                                Facility.Phone.builder()
                                    .fax("771-025-8107")
                                    .main("701-852-0177")
                                    .build())
                            .hours(
                                Facility.Hours.builder()
                                    .monday("730AM-430PM")
                                    .tuesday("730AM-430PM")
                                    .wednesday("730AM-800PM")
                                    .thursday("730AM-430PM")
                                    .friday("730AM-430PM")
                                    .saturday("Closed")
                                    .sunday("Closed")
                                    .build())
                            .operationalHoursSpecialInstructions(
                                "Administrative hours are Monday-Friday 8:00 a.m. to 4:30 p.m. |")
                            .mobile(false)
                            .activeStatus(Facility.ActiveStatus.A)
                            .visn("21")
                            .build())
                    .build()));
  }
}
