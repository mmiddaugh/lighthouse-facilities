package gov.va.api.lighthouse.facilities.api.emergencycontact;

import java.time.Instant;
import java.util.List;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.Facility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.api.v0.Facility.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Value
@Builder
// @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
// @Schema(description = "Facility's or clinic's emergency contact")
public final class UrgentContact {
  String id;

  // @Schema(example = "vha_688")
  // @NotNull
  @JsonProperty("facility_id")
  String facilityId;

  Clinic clinic;

  Contact contact;

  String note;

  @JsonProperty("phone_numbers")
  List<PhoneNumber> phoneNumbers;

  @JsonProperty("last_updated")
  Instant lastUpdated;

  @Value
  @Builder
  public static final class Clinic {
    String name;

    String specialty;
  }

  @Value
  @Builder
  public static final class Contact {
    String name;

    String email;

    PhoneNumber phone;
  }

  @Value
  @Builder
  public static final class PhoneNumber {
    String label;

    String number;

    String extension;
  }
}
