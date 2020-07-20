package gov.va.api.lighthouse.facilities.api.urgentcontact;

import java.time.Instant;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.PageLinks;
import gov.va.api.lighthouse.facilities.api.v0.Facility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.api.v0.Facility.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Value
@Builder(toBuilder = true)
// @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
// @Schema(description = "Facility's or clinic's emergency contact")
public final class UrgentContact {
  @NotBlank String id;

  // @Schema(example = "vha_688")
  @NotBlank
  @JsonProperty("facility_id")
  String facilityId;

  @Valid Clinic clinic;

  @NotNull @Valid Administrator administrator;

  @NotBlank
  @Size(max = 1000)
  String note;

  @Valid
  @Size(min = 3, max = 3)
  @JsonProperty("phone_numbers")
  List<PhoneNumber> phoneNumbers;

  @JsonProperty("last_updated")
  Instant lastUpdated;

  @Value
  @Builder
  public static final class Administrator {
    @NotBlank String name;

    @NotBlank String email;

    @Valid @NotNull PhoneNumber phone;
  }

  @Value
  @Builder
  public static final class Clinic {
    @NotBlank String name;

    @NotBlank String specialty;
  }

  @Value
  @Builder
  public static final class PhoneNumber {
    String label;

    @NotBlank String number;

    String extension;
  }
}
