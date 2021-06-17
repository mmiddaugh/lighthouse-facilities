package gov.va.api.lighthouse.facilities.collector;

import com.google.common.base.Stopwatch;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import us.dustinj.timezonemap.TimeZone;
import us.dustinj.timezonemap.TimeZoneMap;

/**
 * The TimeZoneMap library works by: 1. Generating an 'area' from the SW to NE to calculate on, via
 * lat/long points (TimeZoneMap) 2. Figuring out the time zone from a lat/long point within the map
 * (TimeZone) 3. Translating this point to Olsen time (getZoneId)
 */
@Slf4j
public class CalculateTimeZone {
  // Continental US points to reduce calculation time.
  static final double LatitudeSouthwest = 22.998796;
  static final double LongitudeSouthwest = -127.696638;
  static final double LatitudeNortheast = 50.682197;
  static final double LongitudeNortheast = -65.394911;

  /** Creates a TimeZoneMap for calculating time zones based on a given lat/long. */
  private static TimeZoneMap calculateTimeZoneMap(Double latitude, Double longitude) {
    Stopwatch timer = Stopwatch.createStarted();
    final double regionBoundary = 0.0000001;
    TimeZoneMap map =
        TimeZoneMap.forRegion(
            latitude - regionBoundary,
            longitude - regionBoundary,
            latitude + regionBoundary,
            longitude + regionBoundary);
    log.info(
        "Loading time zone map for external US {}, {} took {} milliseconds.",
        latitude,
        longitude,
        timer.elapsed(TimeUnit.MILLISECONDS));
    return map;
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
      } else {
        log.warn("Time zone calculation failed, unable to calculate mapping.");
      }
    } else {
      log.warn("Time zone calculation failed, longitude or latitude is null.");
    }
    return timeZone;
  }

  /**
   * Calculate and load timezones given longitude and latitude passing in the continental US map. If
   * not within the continental US, calculates it based on the lat/long.
   */
  @SneakyThrows
  public static String calculateTimeZonesWithMap(
      BigDecimal latitudeDecimal, BigDecimal longitudeDecimal, TimeZoneMap timeZoneMap) {
    String timeZone = null;
    TimeZone timeZoneOverlap = null;
    if (longitudeDecimal != null && latitudeDecimal != null) {
      double latitude = latitudeDecimal.doubleValue();
      double longitude = longitudeDecimal.doubleValue();

      // Calculate using the passed continental Time Zone Map
//      if (latitude <= LatitudeNortheast
//          && latitude >= LatitudeSouthwest
//          && longitude <= LongitudeNortheast
//          && longitude >= LongitudeSouthwest) {
        if (timeZoneMap != null) {
          timeZoneOverlap = timeZoneMap.getOverlappingTimeZone(latitude, longitude);
        }
        if (timeZoneOverlap != null) {
          timeZone = timeZoneOverlap.getZoneId();
        } else {
          log.warn("Time zone calculation failed, unable to calculate mapping. [Continental]");
        }
//      } else {
//        // Calculate with generating the Time Zone Map via given lat/long.
//        timeZone = calculateTimeZones(BigDecimal.valueOf(latitude), BigDecimal.valueOf(longitude));
//      }
    }
    return timeZone;
  }

  /** Loads the continental US map. */
  @SneakyThrows
  public static TimeZoneMap continentalUsMap() {
    Stopwatch timer = Stopwatch.createStarted();
//    TimeZoneMap usMap =
//        TimeZoneMap.forRegion(
//            LatitudeSouthwest, LongitudeSouthwest, LatitudeNortheast, LongitudeNortheast);
    TimeZoneMap usMap = TimeZoneMap.forEverywhere();
    log.info(
        "Loading continental US time zone map took {} seconds.", timer.elapsed(TimeUnit.SECONDS));
    return usMap;
  }
}
