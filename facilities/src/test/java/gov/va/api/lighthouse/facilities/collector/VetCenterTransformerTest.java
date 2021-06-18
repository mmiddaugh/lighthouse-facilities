package gov.va.api.lighthouse.facilities.collector;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import org.junit.jupiter.api.Test;

public class VetCenterTransformerTest {
  @Test
  void empty() {
    assertThat(
            VetCenterTransformer.builder()
                .vast(VastEntity.builder().build())
                .websites(emptyMap())
                .timeZoneMap(CalculateTimeZone.getEverywhereTimeZoneMap())
                .build()
                .toFacility())
        .isNull();

    assertThat(
            VetCenterTransformer.builder()
                .vast(VastEntity.builder().stationNumber("x").build())
                .websites(emptyMap())
                .timeZoneMap(CalculateTimeZone.getEverywhereTimeZoneMap())
                .build()
                .toFacility())
        .isEqualTo(Facility.builder().id("vc_x").type(Facility.Type.va_facilities).build());
  }
}
