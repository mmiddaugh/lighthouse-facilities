package gov.va.api.lighthouse.facilities.collector;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import us.dustinj.timezonemap.TimeZoneMap;

@Slf4j
public class TimeZoneMapTest {
  @Test
  /**
   * This test calculates the TimeZoneMap every call, since loading the entire Continental US is
   * extremely time-hungry.
   */
  void timezoneContinentalUSA() {
    CalculateTimeZone.continentalUsMap();
    City anchorage =
        new City(
            BigDecimal.valueOf(61.172881), BigDecimal.valueOf(-149.915261), "America/Anchorage");
    City fresno =
        new City(
            BigDecimal.valueOf(36.737343), BigDecimal.valueOf(-119.771027), "America/Los_Angeles");
    City saltLakeCity =
        new City(BigDecimal.valueOf(40.724780), BigDecimal.valueOf(-111.896381), "America/Denver");
    City newOrleans =
        new City(BigDecimal.valueOf(29.953269), BigDecimal.valueOf(-90.093790), "America/Chicago");
    City bangor =
        new City(BigDecimal.valueOf(44.801035), BigDecimal.valueOf(-68.781614), "America/New_York");
    String anchorageZone =
        Objects.requireNonNull(
            CalculateTimeZone.calculateTimeZones(anchorage.latitude, anchorage.longitude));
    String fresnoZone =
        Objects.requireNonNull(
            CalculateTimeZone.calculateTimeZones(fresno.latitude, fresno.longitude));
    String saltLakeCityZone =
        Objects.requireNonNull(
            CalculateTimeZone.calculateTimeZones(saltLakeCity.latitude, saltLakeCity.longitude));
    String newOrleansZone =
        Objects.requireNonNull(
            CalculateTimeZone.calculateTimeZones(newOrleans.latitude, newOrleans.longitude));
    String bangorZone =
        Objects.requireNonNull(
            CalculateTimeZone.calculateTimeZones(bangor.latitude, bangor.longitude));
    assertThat(anchorageZone).isEqualTo(anchorage.olsenTime);
    assertThat(fresnoZone).isEqualTo(fresno.olsenTime);
    assertThat(saltLakeCityZone).isEqualTo(saltLakeCity.olsenTime);
    assertThat(newOrleansZone).isEqualTo(newOrleans.olsenTime);
    assertThat(bangorZone).isEqualTo(bangor.olsenTime);
  }

  @Test
  /** The default test provided by the library. */
  void timezoneEurope() {
    TimeZoneMap map = TimeZoneMap.forRegion(43.5, 8.0, 53.00, 26.0);
    String berlin = // Returns "Europe/Berlin"
        Objects.requireNonNull(map.getOverlappingTimeZone(52.518424, 13.404776)).getZoneId();
    String prague = // Returns "Europe/Prague"
        Objects.requireNonNull(map.getOverlappingTimeZone(50.074154, 14.437403)).getZoneId();
    String budapest = // Returns "Europe/Budapest"
        Objects.requireNonNull(map.getOverlappingTimeZone(47.49642, 19.04970)).getZoneId();
    String milan = // Returns "Europe/Rome"
        Objects.requireNonNull(map.getOverlappingTimeZone(45.466677, 9.188258)).getZoneId();
    String adriaticSea = // Returns "Etc/GMT-1"
        Objects.requireNonNull(map.getOverlappingTimeZone(44.337, 13.8282)).getZoneId();
    assertThat(berlin).isEqualTo("Europe/Berlin");
    assertThat(prague).isEqualTo("Europe/Prague");
    assertThat(budapest).isEqualTo("Europe/Budapest");
    assertThat(milan).isEqualTo("Europe/Rome");
    assertThat(adriaticSea).isEqualTo("Etc/GMT-1");
  }

  private static class City {
    final BigDecimal latitude;

    final BigDecimal longitude;

    String olsenTime;

    City(BigDecimal latitude, BigDecimal longitude, String olsenTime) {
      this.latitude = latitude;
      this.longitude = longitude;
      this.olsenTime = olsenTime;
    }
  }
}
