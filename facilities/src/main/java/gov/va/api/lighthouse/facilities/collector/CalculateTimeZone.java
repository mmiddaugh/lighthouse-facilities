package gov.va.api.lighthouse.facilities.collector;

import java.math.BigDecimal;
import java.util.Objects;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import us.dustinj.timezonemap.TimeZoneMap;

@Slf4j
public class CalculateTimeZone {
  private static TimeZoneMap calculateTimeZoneMap(double latitude, double longitude) {
    final double regionBoundary = 0.0001;
    return TimeZoneMap.forRegion(
        latitude - regionBoundary,
        longitude - regionBoundary,
        latitude + regionBoundary,
        longitude + regionBoundary);
  }

  /** Calculate and load timezones given longitude and latitude. */
  @SneakyThrows
  public static String calculateTimeZones(BigDecimal latitudeDecimal, BigDecimal longitudeDecimal) {
    String timeZone;
    if (!latitudeDecimal.equals(null) || !longitudeDecimal.equals(null)) {
    final double latitude = latitudeDecimal.doubleValue();
    final double longitude = longitudeDecimal.doubleValue();
    timeZone =
        Objects.requireNonNull(
                calculateTimeZoneMap(latitude, longitude)
                    .getOverlappingTimeZone(latitude, longitude))
            .getZoneId();
    log.info("Calculating time zone for {}, {} is {}.", latitude, longitude, timeZone);
    } else {
      log.warn("Longitude or latitude is null");
      timeZone = "Unable to calculate time zone.";
    }
    return timeZone;
  }
}
