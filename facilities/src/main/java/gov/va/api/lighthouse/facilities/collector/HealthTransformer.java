package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.collector.Transformers.allBlank;
import static gov.va.api.lighthouse.facilities.collector.Transformers.checkAngleBracketNull;
import static gov.va.api.lighthouse.facilities.collector.Transformers.emptyToNull;
import static gov.va.api.lighthouse.facilities.collector.Transformers.hoursToClosed;
import static gov.va.api.lighthouse.facilities.collector.Transformers.isBlank;
import static gov.va.api.lighthouse.facilities.collector.Transformers.phoneTrim;
import static java.util.stream.Collectors.toCollection;
import static org.apache.commons.lang3.StringUtils.compareIgnoreCase;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.length;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.upperCase;

import com.google.common.collect.ListMultimap;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.Facility.PatientWaitTime;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.lang3.BooleanUtils;

@Builder
final class HealthTransformer {
  private static final Map<String, Facility.HealthService> HEALTH_SERVICES =
      initHealthServicesMap();

  @NonNull private final VastEntity vast;

  @NonNull private final ListMultimap<String, AccessToCareEntry> accessToCare;

  @NonNull private final ListMultimap<String, AccessToPwtEntry> accessToPwt;

  @NonNull private final Map<String, String> mentalHealthPhoneNumbers;

  @NonNull private final ListMultimap<String, StopCode> stopCodesMap;

  @NonNull private final Map<String, String> timeZones;

  @NonNull private final Map<String, String> websites;

  private static Map<String, Facility.HealthService> initHealthServicesMap() {
    Map<String, Facility.HealthService> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    map.put("AUDIOLOGY", Facility.HealthService.Audiology);
    map.put("CARDIOLOGY", Facility.HealthService.Cardiology);
    map.put("COMP WOMEN'S HLTH", Facility.HealthService.WomensHealth);
    map.put("DERMATOLOGY", Facility.HealthService.Dermatology);
    map.put("GASTROENTEROLOGY", Facility.HealthService.Gastroenterology);
    map.put("GYNECOLOGY", Facility.HealthService.Gynecology);
    map.put("MENTAL HEALTH", Facility.HealthService.MentalHealthCare);
    map.put("OPHTHALMOLOGY", Facility.HealthService.Ophthalmology);
    map.put("OPTOMETRY", Facility.HealthService.Optometry);
    map.put("ORTHOPEDICS", Facility.HealthService.Orthopedics);
    map.put("PRIMARY CARE", Facility.HealthService.PrimaryCare);
    map.put("SPECIALTY CARE", Facility.HealthService.SpecialtyCare);
    map.put("UROLOGY CLINIC", Facility.HealthService.Urology);
    return map;
  }

  private static Facility.HealthService serviceName(AccessToCareEntry atc) {
    return atc == null ? null : HEALTH_SERVICES.get(trimToEmpty(atc.apptTypeName()));
  }

  private static LocalDate sliceToDate(String slice) {
    return length(slice) <= 9 ? null : LocalDate.parse(slice.substring(0, 10));
  }

  private static Facility.PatientWaitTime waitTime(AccessToCareEntry atc) {
    if (atc == null
        || isBlank(serviceName(atc))
        || allBlank(waitTimeNumber(atc.newWaitTime()), waitTimeNumber(atc.estWaitTime()))) {
      return null;
    }
    return Facility.PatientWaitTime.builder()
        .service(serviceName(atc))
        .newPatientWaitTime(waitTimeNumber(atc.newWaitTime()))
        .establishedPatientWaitTime(waitTimeNumber(atc.estWaitTime()))
        .build();
  }

  private static BigDecimal waitTimeNumber(BigDecimal waitTime) {
    if (waitTime == null || waitTime.compareTo(new BigDecimal("999")) >= 0) {
      return null;
    }
    return waitTime;
  }

  private List<AccessToCareEntry> accessToCareEntries() {
    return accessToCare.get(trimToEmpty(upperCase(id(), Locale.US)));
  }

  private List<AccessToPwtEntry> accessToPwtEntries() {
    return accessToPwt.get(trimToEmpty(upperCase(id(), Locale.US)));
  }

  private Facility.ActiveStatus activeStatus() {
    if (allBlank(vast.pod())) {
      return null;
    }
    return vast.pod().equalsIgnoreCase("A") ? Facility.ActiveStatus.A : Facility.ActiveStatus.T;
  }

  private Facility.Addresses address() {
    if (allBlank(physical())) {
      return null;
    }
    return Facility.Addresses.builder().physical(physical()).build();
  }

  private LocalDate atcEffectiveDate() {
    return accessToCareEntries().stream()
        .map(ace -> sliceToDate(ace.sliceEndDate()))
        .filter(Objects::nonNull)
        .sorted(Comparator.reverseOrder())
        .findFirst()
        .orElse(null);
  }

  private LocalDate atpEffectiveDate() {
    return accessToPwtEntries().stream()
        .map(ape -> sliceToDate(ape.sliceEndDate()))
        .filter(Objects::nonNull)
        .sorted(Comparator.reverseOrder())
        .findFirst()
        .orElse(null);
  }

  private Facility.FacilityAttributes attributes() {
    if (allBlank(
        vast.stationName(),
        classification(),
        website(),
        timeZone(),
        vast.latitude(),
        vast.longitude(),
        address(),
        phone(),
        hours(),
        vast.operationalHoursSpecialInstructions(),
        services(),
        satisfaction(),
        waitTimes(),
        vast.mobile(),
        activeStatus(),
        vast.visn())) {
      return null;
    }
    return Facility.FacilityAttributes.builder()
        .name(vast.stationName())
        .facilityType(Facility.FacilityType.va_health_facility)
        .classification(classification())
        .website(website())
        .timeZone(timeZone())
        .latitude(vast.latitude())
        .longitude(vast.longitude())
        .address(address())
        .phone(phone())
        .hours(hours())
        .operationalHoursSpecialInstructions(vast.operationalHoursSpecialInstructions())
        .services(services())
        .satisfaction(satisfaction())
        .waitTimes(waitTimes())
        .mobile(vast.mobile())
        .activeStatus(activeStatus())
        .visn(vast.visn())
        .build();
  }

  String classification() {
    switch (trimToEmpty(vast.cocClassificationId())) {
      case "1":
        return "VA Medical Center (VAMC)";
      case "2":
        return "Health Care Center (HCC)";
      case "3":
        return "Multi-Specialty CBOC";
      case "4":
        return "Primary Care CBOC";
      case "5":
        return "Other Outpatient Services (OOS)";
      case "7":
        return "Residential Care Site (MH RRTP/DRRTP) (Stand-Alone)";
      case "8":
        return "Extended Care Site (Community Living Center) (Stand-Alone)";
      default:
        if (isNotBlank(vast.cocClassificationId())) {
          return vast.cocClassificationId();
        }
        return vast.abbreviation();
    }
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
    return "vha_" + vast.stationNumber();
  }

  private Facility.Phone phone() {
    String fax = phoneTrim(vast.staFax());
    String main = phoneTrim(vast.staPhone());
    String pharmacy = phoneTrim(vast.pharmacyPhone());
    String afterHours = phoneTrim(vast.afterHoursPhone());
    String patientAdvocate = phoneTrim(vast.patientAdvocatePhone());
    String mentalHealth = phoneTrim(mentalHealthPhoneNumbers.get(id()));
    String enrollmentCoordinator = phoneTrim(vast.enrollmentCoordinatorPhone());
    if (allBlank(
        fax, main, pharmacy, afterHours, patientAdvocate, mentalHealth, enrollmentCoordinator)) {
      return null;
    }
    return Facility.Phone.builder()
        .fax(fax)
        .main(main)
        .pharmacy(pharmacy)
        .afterHours(afterHours)
        .patientAdvocate(patientAdvocate)
        .mentalHealthClinic(mentalHealth)
        .enrollmentCoordinator(enrollmentCoordinator)
        .build();
  }

  private Facility.Address physical() {
    if (allBlank(
        zip(),
        vast.city(),
        vast.state(),
        checkAngleBracketNull(vast.address2()),
        checkAngleBracketNull(vast.address1()),
        checkAngleBracketNull(vast.address3()))) {
      return null;
    }
    // address1 and address2 swapped
    return Facility.Address.builder()
        .zip(zip())
        .city(vast.city())
        .state(upperCase(vast.state(), Locale.US))
        .address1(checkAngleBracketNull(vast.address2()))
        .address2(checkAngleBracketNull(vast.address1()))
        .address3(checkAngleBracketNull(vast.address3()))
        .build();
  }

  private Facility.Satisfaction satisfaction() {
    if (allBlank(satisfactionScores(), atpEffectiveDate())) {
      return null;
    }
    return Facility.Satisfaction.builder()
        .health(satisfactionScores())
        .effectiveDate(atpEffectiveDate())
        .build();
  }

  private BigDecimal satisfactionScore(String type) {
    return accessToPwtEntries().stream()
        .filter(atp -> equalsIgnoreCase(atp.apptTypeName(), type))
        .map(atp -> atp.shepScore())
        .min(Comparator.naturalOrder())
        .orElse(null);
  }

  private Facility.PatientSatisfaction satisfactionScores() {
    if (allBlank(
        satisfactionScore("Primary Care (Urgent)"),
        satisfactionScore("Primary Care (Routine)"),
        satisfactionScore("Specialty Care (Urgent)"),
        satisfactionScore("Specialty Care (Routine)"))) {
      return null;
    }
    return Facility.PatientSatisfaction.builder()
        .primaryCareUrgent(satisfactionScore("Primary Care (Urgent)"))
        .primaryCareRoutine(satisfactionScore("Primary Care (Routine)"))
        .specialtyCareUrgent(satisfactionScore("Specialty Care (Urgent)"))
        .specialtyCareRoutine(satisfactionScore("Specialty Care (Routine)"))
        .build();
  }

  private Facility.Services services() {
    if (allBlank(servicesHealth(), atcEffectiveDate())) {
      return null;
    }
    return Facility.Services.builder()
        .health(servicesHealth())
        .lastUpdated(atcEffectiveDate())
        .build();
  }

  private List<Facility.HealthService> servicesHealth() {
    List<Facility.HealthService> services =
        accessToCareEntries().stream()
            .map(ace -> serviceName(ace))
            .filter(Objects::nonNull)
            .collect(toCollection(ArrayList::new));
    if (accessToCareEntries().stream().anyMatch(ace -> BooleanUtils.isTrue(ace.emergencyCare()))) {
      services.add(Facility.HealthService.EmergencyCare);
    }
    if (accessToCareEntries().stream().anyMatch(ace -> BooleanUtils.isTrue(ace.urgentCare()))) {
      services.add(Facility.HealthService.UrgentCare);
    }
    if (stopCodes().stream().anyMatch(sc -> StopCode.DENTISTRY.contains(trimToEmpty(sc.code())))) {
      services.add(Facility.HealthService.DentalServices);
    }
    if (stopCodes().stream().anyMatch(sc -> StopCode.NUTRITION.contains(trimToEmpty(sc.code())))) {
      services.add(Facility.HealthService.Nutrition);
    }
    if (stopCodes().stream().anyMatch(sc -> StopCode.PODIATRY.contains(trimToEmpty(sc.code())))) {
      services.add(Facility.HealthService.Podiatry);
    }
    Collections.sort(services, (left, right) -> left.name().compareToIgnoreCase(right.name()));
    return emptyToNull(services);
  }

  private List<StopCode> stopCodes() {
    return stopCodesMap.get(trimToEmpty(upperCase(id(), Locale.US)));
  }

  String timeZone() {
    return allBlank(id()) ? null : timeZones.get(id());
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

  private Facility.WaitTimes waitTimes() {
    if (allBlank(waitTimesHealth(), atcEffectiveDate())) {
      return null;
    }
    return Facility.WaitTimes.builder()
        .health(waitTimesHealth())
        .effectiveDate(atcEffectiveDate())
        .build();
  }

  private List<Facility.PatientWaitTime> waitTimesHealth() {
    List<PatientWaitTime> results =
        accessToCareEntries().stream()
            .map(ace -> waitTime(ace))
            .filter(Objects::nonNull)
            .collect(toCollection(ArrayList::new));
    Collections.sort(
        results, (left, right) -> compareIgnoreCase(left.service().name(), right.service().name()));
    return emptyToNull(results);
  }

  String website() {
    return allBlank(id()) ? null : websites.get(id());
  }

  private String zip() {
    String zip = vast.zip();
    String zipPlus4 = vast.zip4();
    if (isNotBlank(zip) && isNotBlank(zipPlus4) && !zipPlus4.matches("^[0]+$")) {
      return zip + "-" + zipPlus4;
    }
    return zip;
  }
}
