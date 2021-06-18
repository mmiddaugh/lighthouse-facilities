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
 * (TimeZone) 3. Translating this point to Olsen time (getZoneId) Alternatively the EVERYWHERE map
 * can be used, though it does take longer to initiate.
 */
@Slf4j
public class CalculateTimeZone {
  /** Calculate and load timezones given longitude and latitude passing in a map. */
  @SneakyThrows
  public static String calculateTimeZonesWithMap(
      BigDecimal latitudeDecimal, BigDecimal longitudeDecimal, TimeZoneMap timeZoneMap) {
    String timeZone = null;
    TimeZone timeZoneOverlap = null;
    if (longitudeDecimal != null && latitudeDecimal != null) {
      if (timeZoneMap != null) {
        timeZoneOverlap =
            timeZoneMap.getOverlappingTimeZone(
                latitudeDecimal.doubleValue(), longitudeDecimal.doubleValue());
      }
      if (timeZoneOverlap != null) {
        timeZone = timeZoneOverlap.getZoneId();
      } else {
        log.warn("Time zone calculation failed, unable to calculate mapping.");
      }
    } else {
      log.warn(
          "Time zone calculation failed, latitude [{}] or longitude [{}] is invalid.",
          latitudeDecimal,
          longitudeDecimal);
    }
    return timeZone;
  }

  /** Loads the EVERYWHERE map. */
  @SneakyThrows
  public static TimeZoneMap getEverywhereTimeZoneMap() {
    Stopwatch timer = Stopwatch.createStarted();
    TimeZoneMap timeZoneMap = TimeZoneMap.forEverywhere();
    log.info(
        "Loading everywhere time zone map took {} milliseconds.",
        timer.elapsed(TimeUnit.MILLISECONDS));
    return timeZoneMap;
  }
}
