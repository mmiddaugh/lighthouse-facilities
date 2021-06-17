package gov.va.api.lighthouse.facilities.collector;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import us.dustinj.timezonemap.TimeZoneMap;

@Builder
@Slf4j
@SuppressWarnings("ObjectToString")
final class BenefitsCollector {
  @NonNull private final Map<String, String> websites;

  @NonNull private final JdbcTemplate jdbcTemplate;

  @NonNull private final TimeZoneMap timeZoneMap;

  /** Convert the results into a CdwBenefits Object. */
  @SneakyThrows
  static CdwBenefits toCdwBenefits(ResultSet rs) {
    return CdwBenefits.builder()
        .facilityName(rs.getString("FACILITY_NAME"))
        .facilityNumber(rs.getString("FACILITY_NUMBER"))
        .facilityType(rs.getString("FACILITY_TYPE"))
        .address1(rs.getString("ADDRESS_1"))
        .address2(rs.getString("ADDRESS_2"))
        .city(rs.getString("CITY"))
        .state(rs.getString("STATE"))
        .zip(rs.getString("ZIP"))
        .fax(rs.getString("FAX"))
        .phone(rs.getString("PHONE"))
        .monday(rs.getString("MONDAY"))
        .tuesday(rs.getString("TUESDAY"))
        .wednesday(rs.getString("WEDNESDAY"))
        .thursday(rs.getString("THURSDAY"))
        .friday(rs.getString("FRIDAY"))
        .saturday(rs.getString("SATURDAY"))
        .sunday(rs.getString("SUNDAY"))
        .applyingForBenefits(rs.getString("APPLYING_FOR_BENEFITS"))
        .burialClaimAssistance(rs.getString("BURIAL_CLAIM_ASSISTANCE"))
        .disabilityClaimAssistance(rs.getString("DISABILITY_CLAIM_ASSISTANCE"))
        .ebenefitsRegistration(rs.getString("EBENEFITS_REGISTRATION"))
        .educationAndCareerCounseling(rs.getString("EDUCATION_AND_CAREER_COUNSELING"))
        .educationClaimAssistance(rs.getString("EDUCATION_CLAIM_ASSISTANCE"))
        .familyMemberClaimAssistance(rs.getString("FAMILY_MEMBER_CLAIM_ASSISTANCE"))
        .homelessAssistance(rs.getString("HOMELESS_ASSISTANCE"))
        .vaHomeLoanAssistance(rs.getString("VA_HOME_LOAN_ASSISTANCE"))
        .insuranceClaimAssistance(rs.getString("INSURANCE_CLAIM_ASSISTANCE"))
        .integratedDisabilityEvaluationSystem(rs.getString("IDES"))
        .preDischargeClaimAssistance(rs.getString("PRE_DISCHARGE_CLAIM_ASSISTANCE"))
        .transitionAssistance(rs.getString("TRANSITION_ASSISTANCE"))
        .updatingDirectDepositInformation(rs.getString("UPDATING_DIRECT_DEPOSIT_INFORMA"))
        .vocationalRehabilitationEmplo(rs.getString("VOCATIONAL_REHABILITATION_EMPLO"))
        .otherServices(rs.getString("OTHER_SERVICES"))
        .websiteUrl(rs.getString("WEBSITE_URL"))
        .latitude(rs.getBigDecimal("LAT"))
        .longitude(rs.getBigDecimal("LONG"))
        .build();
  }

  /** Collects and transforms all benefits into a list of facilities. */
  public Collection<Facility> collect() {
    try {
      return requestCdwBenefits().stream()
          .map(
              facility ->
                  BenefitsTransformer.builder()
                      .cdwFacility(facility)
                      .csvWebsite(websites.get("vba_" + facility.facilityNumber()))
                      .timeZoneMap(timeZoneMap)
                      .build()
                      .toFacility())
          .collect(toList());
    } catch (Exception e) {
      throw new CollectorExceptions.BenefitsCollectorException(e);
    }
  }

  @SneakyThrows
  private List<CdwBenefits> requestCdwBenefits() {
    final Stopwatch totalWatch = Stopwatch.createStarted();
    List<CdwBenefits> cdwBenefits =
        ImmutableList.copyOf(
            jdbcTemplate.query(
                "SELECT "
                    + "FACILITY_NAME,"
                    + "FACILITY_NUMBER,"
                    + "FACILITY_TYPE,"
                    + "ADDRESS_1,"
                    + "ADDRESS_2,"
                    + "CITY,"
                    + "STATE,"
                    + "ZIP,"
                    + "FAX,"
                    + "PHONE,"
                    + "MONDAY,"
                    + "TUESDAY,"
                    + "WEDNESDAY,"
                    + "THURSDAY,"
                    + "FRIDAY,"
                    + "SATURDAY,"
                    + "SUNDAY,"
                    + "APPLYING_FOR_BENEFITS,"
                    + "BURIAL_CLAIM_ASSISTANCE,"
                    + "DISABILITY_CLAIM_ASSISTANCE,"
                    + "EBENEFITS_REGISTRATION,"
                    + "EDUCATION_AND_CAREER_COUNSELING,"
                    + "EDUCATION_CLAIM_ASSISTANCE,"
                    + "FAMILY_MEMBER_CLAIM_ASSISTANCE,"
                    + "HOMELESS_ASSISTANCE,"
                    + "VA_HOME_LOAN_ASSISTANCE,"
                    + "INSURANCE_CLAIM_ASSISTANCE,"
                    + "IDES,"
                    + "PRE_DISCHARGE_CLAIM_ASSISTANCE,"
                    + "TRANSITION_ASSISTANCE,"
                    + "UPDATING_DIRECT_DEPOSIT_INFORMA,"
                    + "VOCATIONAL_REHABILITATION_EMPLO,"
                    + "OTHER_SERVICES,"
                    + "LAT,"
                    + "LONG,"
                    + "WEBSITE_URL"
                    + " FROM App.FacilityLocator_VBA",
                (rs, rowNum) -> toCdwBenefits(rs)));
    log.info(
        "Loading benefits facilities took {} millis for {} entries",
        totalWatch.stop().elapsed(TimeUnit.MILLISECONDS),
        cdwBenefits.size());
    checkState(!cdwBenefits.isEmpty(), "No App.FacilityLocator_VBA entries");
    return cdwBenefits;
  }
}
