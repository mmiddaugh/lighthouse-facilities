package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.CaregiverSupport;
import static gov.va.api.lighthouse.facilities.collector.FacilitiesCollector.loadCaregiverSupport;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ArrayListMultimap;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class HealthTransformerTest {

  private static final String CSC_STATIONS_RESOURCE_NAME = "csc_stations.txt";

  private void assertClassification(String classificationId, String featureCode, String expected) {
    assertThat(
            HealthTransformer.builder()
                .vast(
                    VastEntity.builder()
                        .cocClassificationId(classificationId)
                        .abbreviation(featureCode)
                        .build())
                .accessToCare(ArrayListMultimap.create())
                .accessToPwt(ArrayListMultimap.create())
                .cscFacilities(new ArrayList<>())
                .mentalHealthPhoneNumbers(emptyMap())
                .stopCodesMap(ArrayListMultimap.create())
                .websites(emptyMap())
                .timeZoneMap(CalculateTimeZone.getEverywhereTimeZoneMap())
                .build()
                .classification())
        .isEqualTo(expected);
  }

  @Test
  void classification() {
    assertThat(
            HealthTransformer.builder()
                .vast(VastEntity.builder().build())
                .accessToCare(ArrayListMultimap.create())
                .accessToPwt(ArrayListMultimap.create())
                .cscFacilities(new ArrayList<>())
                .mentalHealthPhoneNumbers(emptyMap())
                .stopCodesMap(ArrayListMultimap.create())
                .websites(emptyMap())
                .timeZoneMap(CalculateTimeZone.getEverywhereTimeZoneMap())
                .build()
                .classification())
        .isNull();
    assertClassification(null, null, null);
    assertClassification("1", null, "VA Medical Center (VAMC)");
    assertClassification("2", null, "Health Care Center (HCC)");
    assertClassification("3", null, "Multi-Specialty CBOC");
    assertClassification("4", null, "Primary Care CBOC");
    assertClassification("5", null, "Other Outpatient Services (OOS)");
    assertClassification("7", null, "Residential Care Site (MH RRTP/DRRTP) (Stand-Alone)");
    assertClassification("8", null, "Extended Care Site (Community Living Center) (Stand-Alone)");
    assertClassification("x", null, "x");
    assertClassification(null, "f", "f");
  }

  @Test
  void empty() {
    assertThat(
            HealthTransformer.builder()
                .vast(VastEntity.builder().build())
                .accessToCare(ArrayListMultimap.create())
                .accessToPwt(ArrayListMultimap.create())
                .cscFacilities(new ArrayList<>())
                .mentalHealthPhoneNumbers(emptyMap())
                .stopCodesMap(ArrayListMultimap.create())
                .websites(emptyMap())
                .timeZoneMap(CalculateTimeZone.getEverywhereTimeZoneMap())
                .build()
                .toFacility())
        .isNull();
    ArrayListMultimap<String, AccessToCareEntry> atc = ArrayListMultimap.create();
    atc.put("VHA_X", AccessToCareEntry.builder().build());
    ArrayListMultimap<String, AccessToPwtEntry> atp = ArrayListMultimap.create();
    atp.put("VHA_X", AccessToPwtEntry.builder().build());
    ArrayListMultimap<String, StopCode> sc = ArrayListMultimap.create();
    sc.put("VHA_X", StopCode.builder().build());
    assertThat(
            HealthTransformer.builder()
                .vast(VastEntity.builder().stationNumber("x").build())
                .accessToCare(atc)
                .accessToPwt(atp)
                .cscFacilities(new ArrayList<>())
                .mentalHealthPhoneNumbers(emptyMap())
                .stopCodesMap(sc)
                .websites(emptyMap())
                .timeZoneMap(CalculateTimeZone.getEverywhereTimeZoneMap())
                .build()
                .toFacility())
        .isEqualTo(Facility.builder().id("vha_x").type(Facility.Type.va_facilities).build());
  }

  @Test
  void facilityWithCaregiverSupport() {
    ArrayListMultimap<String, AccessToCareEntry> atc = ArrayListMultimap.create();
    atc.put("VHA_689", AccessToCareEntry.builder().build());
    ArrayListMultimap<String, AccessToPwtEntry> atp = ArrayListMultimap.create();
    atp.put("VHA_689", AccessToPwtEntry.builder().build());
    ArrayListMultimap<String, StopCode> sc = ArrayListMultimap.create();
    sc.put("VHA_689", StopCode.builder().build());
    assertThat(
            HealthTransformer.builder()
                .vast(VastEntity.builder().stationNumber("689").build())
                .accessToCare(atc)
                .accessToPwt(atp)
                .cscFacilities(loadCaregiverSupport(CSC_STATIONS_RESOURCE_NAME))
                .mentalHealthPhoneNumbers(emptyMap())
                .stopCodesMap(sc)
                .websites(emptyMap())
                .timeZoneMap(CalculateTimeZone.getEverywhereTimeZoneMap())
                .build()
                .toFacility())
        .isEqualTo(
            Facility.builder()
                .id("vha_689")
                .type(Facility.Type.va_facilities)
                .attributes(
                    Facility.FacilityAttributes.builder()
                        .facilityType(Facility.FacilityType.va_health_facility)
                        .services(
                            Facility.Services.builder().health(List.of(CaregiverSupport)).build())
                        .build())
                .build());
  }
}
