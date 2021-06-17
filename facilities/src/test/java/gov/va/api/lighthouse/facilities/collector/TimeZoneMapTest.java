package gov.va.api.lighthouse.facilities.collector;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import us.dustinj.timezonemap.TimeZoneMap;

@Slf4j
public class TimeZoneMapTest {
  @Test
  void invalidLatitudeLongitude() {
    TimeZoneMap usMap = TimeZoneMap.forEverywhere();
    String invalidLatitudeZone =
        CalculateTimeZone.calculateTimeZonesWithMap(null, BigDecimal.valueOf(149.915261), usMap);
    String invalidLongitudeZone =
        CalculateTimeZone.calculateTimeZonesWithMap(BigDecimal.valueOf(36.737343), null, usMap);
    assertThat(invalidLatitudeZone).isNull();
    assertThat(invalidLongitudeZone).isNull();
  }

  @Test
  void invalidMap() {
    String invalidLatitudeZone =
        CalculateTimeZone.calculateTimeZonesWithMap(
            BigDecimal.valueOf(36.737343), BigDecimal.valueOf(149.915261), null);
    assertThat(invalidLatitudeZone).isNull();
  }

  @Test
  void timezoneContinentalUSA() {
    TimeZoneMap usMap = TimeZoneMap.forEverywhere();
    String anchorageZone =
        CalculateTimeZone.calculateTimeZonesWithMap(
            BigDecimal.valueOf(61.2181), BigDecimal.valueOf(-149.915261), usMap);
    String fresnoZone =
        CalculateTimeZone.calculateTimeZonesWithMap(
            BigDecimal.valueOf(36.737343), BigDecimal.valueOf(-119.771027), usMap);
    String saltLakeCityZone =
        CalculateTimeZone.calculateTimeZonesWithMap(
            BigDecimal.valueOf(40.724780), BigDecimal.valueOf(-111.896381), usMap);
    String newOrleansZone =
        CalculateTimeZone.calculateTimeZonesWithMap(
            BigDecimal.valueOf(29.953269), BigDecimal.valueOf(-90.093790), usMap);
    String bangorZone =
        CalculateTimeZone.calculateTimeZonesWithMap(
            BigDecimal.valueOf(44.801035), BigDecimal.valueOf(-68.781614), usMap);
    assertThat(anchorageZone).isEqualTo("America/Anchorage");
    assertThat(fresnoZone).isEqualTo("America/Los_Angeles");
    assertThat(saltLakeCityZone).isEqualTo("America/Denver");
    assertThat(newOrleansZone).isEqualTo("America/Chicago");
    assertThat(bangorZone).isEqualTo("America/New_York");
  }

  @Test
  /** The default test provided by the library. */
  void timezoneEurope() {
    TimeZoneMap map = TimeZoneMap.forRegion(43.5, 8.0, 53.00, 26.0);
    // Returns "Europe/Berlin"
    String berlin =
        Objects.requireNonNull(map.getOverlappingTimeZone(52.518424, 13.404776)).getZoneId();
    // Returns "Europe/Prague"
    String prague =
        Objects.requireNonNull(map.getOverlappingTimeZone(50.074154, 14.437403)).getZoneId();
    // Returns "Europe/Budapest"
    String budapest =
        Objects.requireNonNull(map.getOverlappingTimeZone(47.49642, 19.04970)).getZoneId();
    // Returns "Europe/Rome"
    String milan =
        Objects.requireNonNull(map.getOverlappingTimeZone(45.466677, 9.188258)).getZoneId();
    // Returns "Etc/GMT-1"
    String adriaticSea =
        Objects.requireNonNull(map.getOverlappingTimeZone(44.337, 13.8282)).getZoneId();
    assertThat(berlin).isEqualTo("Europe/Berlin");
    assertThat(prague).isEqualTo("Europe/Prague");
    assertThat(budapest).isEqualTo("Europe/Budapest");
    assertThat(milan).isEqualTo("Europe/Rome");
    assertThat(adriaticSea).isEqualTo("Etc/GMT-1");
  }
}
