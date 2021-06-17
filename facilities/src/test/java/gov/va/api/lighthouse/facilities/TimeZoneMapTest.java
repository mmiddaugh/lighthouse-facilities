package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import us.dustinj.timezonemap.TimeZoneMap;

public class TimeZoneMapTest {
  public TimeZoneMap calculateTimeZoneMap(double latitude, double longitude) {
    double regionBoundary = 0.0001;
    return TimeZoneMap.forRegion(
        latitude - regionBoundary,
        longitude - regionBoundary,
        latitude + regionBoundary,
        longitude + regionBoundary);
  }

  @Test
  /**
   * This test calculates the TimeZoneMap every call, since loading the entire Continental US is
   * extremely time-hungry.
   */
  void timezoneContinentalUSA() {
    City anchorage = new City(61.172881, -149.915261, "America/Anchorage");
    City fresno = new City(36.737343, -119.771027, "America/Los_Angeles");
    City saltLakeCity = new City(40.724780, -111.896381, "America/Denver");
    City newOrleans = new City(29.953269, -90.093790, "America/Chicago");
    City bangor = new City(44.801035, -68.781614, "America/New_York");
    String anchorageZone =
        calculateTimeZoneMap(anchorage.latitude, anchorage.longitude)
            .getOverlappingTimeZone(anchorage.latitude, anchorage.longitude)
            .getZoneId();
    String fresnoZone =
        calculateTimeZoneMap(fresno.latitude, fresno.longitude)
            .getOverlappingTimeZone(fresno.latitude, fresno.longitude)
            .getZoneId();
    String saltLakeCityZone =
        calculateTimeZoneMap(saltLakeCity.latitude, saltLakeCity.longitude)
            .getOverlappingTimeZone(saltLakeCity.latitude, saltLakeCity.longitude)
            .getZoneId();
    String newOrleansZone =
        calculateTimeZoneMap(newOrleans.latitude, newOrleans.longitude)
            .getOverlappingTimeZone(newOrleans.latitude, newOrleans.longitude)
            .getZoneId();
    String bangorZone =
        calculateTimeZoneMap(bangor.latitude, bangor.longitude)
            .getOverlappingTimeZone(bangor.latitude, bangor.longitude)
            .getZoneId();
    assertThat(anchorageZone == anchorage.olsenTime);
    assertThat(fresnoZone == fresno.olsenTime);
    assertThat(saltLakeCityZone == saltLakeCity.olsenTime);
    assertThat(newOrleansZone == newOrleans.olsenTime);
    assertThat(bangorZone == bangor.olsenTime);
  }

  @Test
  /** The default test provided by the library. */
  void timezoneEurope() {
    TimeZoneMap map = TimeZoneMap.forRegion(43.5, 8.0, 53.00, 26.0);
    String berlin = // Returns "Europe/Berlin"
        map.getOverlappingTimeZone(52.518424, 13.404776).getZoneId();
    String prague = // Returns "Europe/Prague"
        map.getOverlappingTimeZone(50.074154, 14.437403).getZoneId();
    String budapest = // Returns "Europe/Budapest"
        map.getOverlappingTimeZone(47.49642, 19.04970).getZoneId();
    String milan = // Returns "Europe/Rome"
        map.getOverlappingTimeZone(45.466677, 9.188258).getZoneId();
    String adriaticSea = // Returns "Etc/GMT-1"
        map.getOverlappingTimeZone(44.337, 13.8282).getZoneId();
    assertThat(berlin == "Europe/Berlin");
    assertThat(prague == "Europe/Prague");
    assertThat(budapest == "Europe/Budapest");
    assertThat(milan == "Europe/Rome");
    assertThat(adriaticSea == "Etc/GMT-1");
  }

  private static class City {
    final double latitude;

    double longitude;

    String olsenTime;

    City(double latitude, double longitude, String olsenTime) {
      this.latitude = latitude;
      this.longitude = longitude;
      this.olsenTime = olsenTime;
    }
  }
}
