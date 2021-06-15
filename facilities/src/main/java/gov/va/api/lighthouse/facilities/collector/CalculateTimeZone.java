package gov.va.api.lighthouse.facilities.collector;

import java.math.BigDecimal;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import us.dustinj.timezonemap.TimeZone;
import us.dustinj.timezonemap.TimeZoneMap;

@Slf4j
public class CalculateTimeZone {
  private static TimeZoneMap calculateTimeZoneMap(Double latitude, Double longitude) {
    final double regionBoundary = 0.0000001;
    return TimeZoneMap.forRegion(
        latitude - regionBoundary,
        longitude - regionBoundary,
        latitude + regionBoundary,
        longitude + regionBoundary);
  }

  /** Calculate and load timezones given longitude and latitude. */
  @SneakyThrows
  public static String calculateTimeZones(BigDecimal latitudeDecimal, BigDecimal longitudeDecimal) {
    String timeZone = null;
    TimeZone timeZoneOverlap = null;
    if (latitudeDecimal != null && longitudeDecimal != null) {
      double latitude = latitudeDecimal.doubleValue();
      double longitude = longitudeDecimal.doubleValue();
      TimeZoneMap timeZoneMap = calculateTimeZoneMap(latitude, longitude);
      if (timeZoneMap != null) {
        timeZoneOverlap = timeZoneMap.getOverlappingTimeZone(latitude, longitude);
      }
      if (timeZoneOverlap != null) {
        timeZone = timeZoneOverlap.getZoneId();
        log.info("Calculated time zone {} for {}, {}.", timeZone, latitude, longitude);
      } else {
        log.warn("Time zone calculation failed, unable to calculate mapping.");
      }
    } else {
      log.warn("Time zone calculation failed, longitude or latitude is null.");
    }
    return timeZone;
  }
}
