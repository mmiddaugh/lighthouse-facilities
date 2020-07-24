package gov.va.api.lighthouse.facilitiescollector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
class ArcGisBenefits {
  @Builder.Default List<Feature> features = new ArrayList<>();

  /* Unused Fields:
   * objectIdFieldName
   * uniqueIdField
   *   name
   *   isSystemMaintained
   * globalIdFieldName
   * geometryType
   * spatialReference
   *   wkid
   *   latestWkid
   * fields
   *   name
   *   type
   *   alias
   *   sqlType
   *   length
   *   domain
   *   defaultValue
   */
  @JsonIgnoreProperties(ignoreUnknown = true)
  static final class ArcGisBenefitsBuilder {}

  @Value
  @Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  static class Attributes {
    @JsonProperty("Facility_Name")
    String facilityName;

    @JsonProperty("Facility_Number")
    String facilityNumber;

    @JsonProperty("Facility_Type")
    String facilityType;

    @JsonProperty("Address_1")
    String address1;

    @JsonProperty("Address_2")
    String address2;

    @JsonProperty("City")
    String city;

    @JsonProperty("State")
    String state;

    @JsonProperty("Zip")
    String zip;

    @JsonProperty("Fax")
    String fax;

    @JsonProperty("Phone")
    String phone;

    @JsonProperty("Monday")
    String monday;

    @JsonProperty("Tuesday")
    String tuesday;

    @JsonProperty("Wednesday")
    String wednesday;

    @JsonProperty("Thursday")
    String thursday;

    @JsonProperty("Friday")
    String friday;

    @JsonProperty("Saturday")
    String saturday;

    @JsonProperty("Sunday")
    String sunday;

    @JsonProperty("Applying_for_Benefits")
    @Builder.Default
    String applyingForBenefits = "NO";

    @JsonProperty("Burial_Claim_assistance")
    @Builder.Default
    String burialClaimAssistance = "NO";

    @JsonProperty("Disability_Claim_assistance")
    @Builder.Default
    String disabilityClaimAssistance = "NO";

    @JsonProperty("eBenefits_Registration")
    @Builder.Default
    String ebenefitsRegistration = "NO";

    @JsonProperty("Education_and_Career_Counseling")
    @Builder.Default
    String educationAndCareerCounseling = "NO";

    @JsonProperty("Education_Claim_Assistance")
    @Builder.Default
    String educationClaimAssistance = "NO";

    @JsonProperty("Family_Member_Claim_Assistance")
    @Builder.Default
    String familyMemberClaimAssistance = "NO";

    @JsonProperty("Homeless_Assistance")
    @Builder.Default
    String homelessAssistance = "NO";

    @JsonProperty("VA_Home_Loan_Assistance")
    @Builder.Default
    String vaHomeLoanAssistance = "NO";

    @JsonProperty("Insurance_Claim_Assistance")
    @Builder.Default
    String insuranceClaimAssistance = "NO";

    @JsonProperty("IDES")
    @Builder.Default
    String integratedDisabilityEvaluationSystem = "NO";

    @JsonProperty("Pre_Discharge_Claim_Assistance")
    @Builder.Default
    String preDischargeClaimAssistance = "NO";

    @JsonProperty("Transition_Assistance")
    @Builder.Default
    String transitionAssistance = "NO";

    @JsonProperty("Updating_Direct_Deposit_Informa")
    @Builder.Default
    String updatingDirectDepositInformation = "NO";

    @JsonProperty("Vocational_Rehabilitation_Emplo")
    @Builder.Default
    String vocationalRehabilitationEmplo = "NO";

    @JsonProperty("Other_Services")
    String otherServices;

    @JsonProperty("Website_URL")
    String websiteUrl;

    /* Unused Fields:
     * OBJECTID
     * Comments
     * Lat
     * Long
     * Organization
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    static final class AttributesBuilder {}
  }

  @Value
  @Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  static class Feature {
    Attributes attributes;

    Geometry geometry;

    @JsonIgnoreProperties(ignoreUnknown = true)
    static final class FeatureBuilder {}
  }

  @Value
  @Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  static class Geometry {
    @JsonProperty("x")
    BigDecimal longitude;

    @JsonProperty("y")
    BigDecimal latitude;

    @JsonIgnoreProperties(ignoreUnknown = true)
    static final class GeometryBuilder {}
  }
}
