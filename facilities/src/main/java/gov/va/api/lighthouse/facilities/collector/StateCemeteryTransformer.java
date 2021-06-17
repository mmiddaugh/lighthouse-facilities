package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.collector.Transformers.allBlank;
import static gov.va.api.lighthouse.facilities.collector.Transformers.checkAngleBracketNull;
import static gov.va.api.lighthouse.facilities.collector.Transformers.phoneTrim;
import static org.apache.commons.lang3.StringUtils.indexOf;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.upperCase;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.NonNull;
import us.dustinj.timezonemap.TimeZoneMap;

@Builder
@SuppressWarnings("ObjectToString")
final class StateCemeteryTransformer {
  private static final Pattern ZIP_PATTERN =
      Pattern.compile(".*(\\d{5}-\\d{4}$)|.*(\\d{9}$)|.*(\\d{5}$)");

  @NonNull private final TimeZoneMap timeZoneMap;

  @NonNull private final StateCemeteries.StateCemetery xml;

  @NonNull private final Map<String, String> websites;

  static Facility.Address asAddress(String state, String line1, String line2, String line3) {
    // Zip and city must be parsed out of the last nonblank line
    if (isNotBlank(line3)) {
      if (allBlank(parseZip(line3), parseCity(line3), line1, line2)) {
        return null;
      }
      return Facility.Address.builder()
          .zip(parseZip(line3))
          .city(parseCity(line3))
          .state(state)
          .address1(line1)
          .address2(line2)
          .build();
    }
    if (isNotBlank(line2)) {
      if (allBlank(parseZip(line2), parseCity(line2), line1)) {
        return null;
      }
      return Facility.Address.builder()
          .zip(parseZip(line2))
          .city(parseCity(line2))
          .state(state)
          .address1(line1)
          .build();
    }
    if (isNotBlank(line1)) {
      if (allBlank(parseZip(line1), parseCity(line1))) {
        return Facility.Address.builder().address1(line1).build();
      }
      return Facility.Address.builder()
          .zip(parseZip(line1))
          .city(parseCity(line1))
          .state(state)
          .build();
    }
    return null;
  }

  private static String parseCity(String addressLine) {
    int comma = indexOf(addressLine, ",");
    if (comma <= -1) {
      return null;
    }
    return addressLine.substring(0, comma);
  }

  private static String parseZip(String addressLine) {
    Matcher matcher = ZIP_PATTERN.matcher(trimToEmpty(addressLine));
    if (!matcher.find()) {
      return null;
    }
    return Stream.of(matcher.group(1), matcher.group(2), matcher.group(3))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  private Facility.Addresses address() {
    if (allBlank(mailing(), physical())) {
      return null;
    }
    return Facility.Addresses.builder().mailing(mailing()).physical(physical()).build();
  }

  private Facility.FacilityAttributes attributes() {
    if (allBlank(xml.name(), website(), latitude(), longitude(), address(), phone())) {
      return null;
    }
    return Facility.FacilityAttributes.builder()
        .name(xml.name())
        .facilityType(Facility.FacilityType.va_cemetery)
        .classification("State Cemetery")
        .website(website())
        .latitude(latitude())
        .longitude(longitude())
        .timeZone(CalculateTimeZone.calculateTimeZonesWithMap(latitude(), longitude(), timeZoneMap))
        .address(address())
        .phone(phone())
        .hours(defaultHours())
        .build();
  }

  private Facility.Hours defaultHours() {
    String hours = "Sunrise - Sunset";
    return Facility.Hours.builder()
        .monday(hours)
        .tuesday(hours)
        .wednesday(hours)
        .thursday(hours)
        .friday(hours)
        .saturday(hours)
        .sunday(hours)
        .build();
  }

  private String id() {
    if (allBlank(xml.id())) {
      return null;
    }
    return "nca_s" + xml.id();
  }

  private BigDecimal latitude() {
    if (allBlank(xml.latitude())) {
      return null;
    }
    return new BigDecimal(xml.latitude());
  }

  private BigDecimal longitude() {
    if (allBlank(xml.longitude())) {
      return null;
    }
    return new BigDecimal(xml.longitude());
  }

  private Facility.Address mailing() {
    return asAddress(
        upperCase(xml.stateCode(), Locale.US),
        checkAngleBracketNull(xml.mailingLine1()),
        checkAngleBracketNull(xml.mailingLine2()),
        xml.mailingLine3());
  }

  private Facility.Phone phone() {
    String fax = phoneTrim(xml.fax());
    String main = phoneTrim(xml.phone());
    if (allBlank(fax, main)) {
      return null;
    }
    return Facility.Phone.builder().fax(fax).main(main).build();
  }

  private Facility.Address physical() {
    return asAddress(
        upperCase(xml.stateCode(), Locale.US),
        checkAngleBracketNull(xml.addressLine1()),
        checkAngleBracketNull(xml.addressLine2()),
        xml.addressLine3());
  }

  Facility toFacility() {
    if (allBlank(id())) {
      return null;
    }
    return Facility.builder()
        .id(id())
        .type(Facility.Type.va_facilities)
        .attributes(attributes())
        .build();
  }

  String website() {
    if (isNotBlank(xml.url())) {
      return xml.url();
    }
    if (allBlank(id())) {
      return null;
    }
    return websites.get(id());
  }
}
