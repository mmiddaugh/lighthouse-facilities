package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.collector.Transformers.allBlank;
import static gov.va.api.lighthouse.facilities.collector.Transformers.checkAngleBracketNull;
import static gov.va.api.lighthouse.facilities.collector.Transformers.hoursToClosed;
import static gov.va.api.lighthouse.facilities.collector.Transformers.phoneTrim;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.upperCase;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.Locale;
import java.util.Map;
import lombok.Builder;
import lombok.NonNull;

@Builder
final class VetCenterTransformer {
  @NonNull private final VastEntity vast;

  @NonNull private final Map<String, String> websites;

  private Facility.ActiveStatus activeStatus() {
    if (allBlank(vast.pod())) {
      return null;
    }
    return vast.pod().equalsIgnoreCase("A") ? Facility.ActiveStatus.A : Facility.ActiveStatus.T;
  }

  private Facility.Addresses address() {
    if (allBlank(addressPhysical())) {
      return null;
    }
    return Facility.Addresses.builder().physical(addressPhysical()).build();
  }

  private Facility.Address addressPhysical() {
    // address1 is repeat of station name
    if (allBlank(
        addressZip(),
        vast.city(),
        vast.state(),
        checkAngleBracketNull(vast.address2()),
        checkAngleBracketNull(vast.address3()))) {
      return null;
    }
    return Facility.Address.builder()
        .zip(addressZip())
        .city(vast.city())
        .state(upperCase(vast.state(), Locale.US))
        .address1(checkAngleBracketNull(vast.address2()))
        .address2(checkAngleBracketNull(vast.address3()))
        .build();
  }

  private String addressZip() {
    String zip = vast.zip();
    String zipPlus4 = vast.zip4();
    if (isNotBlank(zip) && isNotBlank(zipPlus4) && !zipPlus4.matches("^[0]+$")) {
      return zip + "-" + zipPlus4;
    }
    return zip;
  }

  private Facility.FacilityAttributes attributes() {
    if (allBlank(
        vast.stationName(),
        website(),
        vast.latitude(),
        vast.longitude(),
        address(),
        phone(),
        hours(),
        vast.operationalHoursSpecialInstructions(),
        vast.mobile(),
        activeStatus(),
        vast.visn())) {
      return null;
    }
    return Facility.FacilityAttributes.builder()
        .name(vast.stationName())
        .facilityType(Facility.FacilityType.vet_center)
        .website(website())
        .latitude(vast.latitude())
        .longitude(vast.longitude())
        .timeZone(CalculateTimeZone.calculateTimeZones(vast.latitude(), vast.longitude()))
        .address(address())
        .phone(phone())
        .hours(hours())
        .operationalHoursSpecialInstructions(vast.operationalHoursSpecialInstructions())
        .mobile(vast.mobile())
        .activeStatus(activeStatus())
        .visn(vast.visn())
        .build();
  }

  private Facility.Hours hours() {
    String mon = hoursToClosed(vast.monday());
    String tue = hoursToClosed(vast.tuesday());
    String wed = hoursToClosed(vast.wednesday());
    String thu = hoursToClosed(vast.thursday());
    String fri = hoursToClosed(vast.friday());
    String sat = hoursToClosed(vast.saturday());
    String sun = hoursToClosed(vast.sunday());
    if (allBlank(mon, tue, wed, thu, fri, sat, sun)) {
      return null;
    }
    return Facility.Hours.builder()
        .monday(mon)
        .tuesday(tue)
        .wednesday(wed)
        .thursday(thu)
        .friday(fri)
        .saturday(sat)
        .sunday(sun)
        .build();
  }

  private String id() {
    if (allBlank(vast.stationNumber())) {
      return null;
    }
    return "vc_" + vast.stationNumber();
  }

  private Facility.Phone phone() {
    String fax = phoneTrim(vast.staFax());
    String main = phoneTrim(vast.staPhone());
    if (allBlank(fax, main)) {
      return null;
    }
    return Facility.Phone.builder().fax(fax).main(main).build();
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
    return allBlank(id()) ? null : websites.get(id());
  }
}
