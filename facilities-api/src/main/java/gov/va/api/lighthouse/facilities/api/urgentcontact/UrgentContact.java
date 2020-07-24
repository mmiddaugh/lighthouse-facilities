package gov.va.api.lighthouse.facilities.api.urgentcontact;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Schema(description = "Urgent contact phone numbers for a clinic")
public final class UrgentContact {
  @NotBlank
  @Schema(example = "12975")
  String id;

  @Schema(example = "vha_534GF")
  @NotBlank
  @JsonProperty("facility_id")
  String facilityId;

  @Schema(example = "DR ANDERSON WH VIDEO CLINIC")
  @JsonProperty("clinic_name")
  @NotBlank
  String clinicName;

  @Schema(example = "PRIMARY CARE/MEDICINE")
  @JsonProperty("clinic_specialty")
  @NotBlank
  String clinicSpecialty;

  @NotNull @Valid Administrator administrator;

  @NotBlank
  @Size(max = 1000)
  @Schema(example = "second door on the right")
  String note;

  @Valid
  @Size(min = 3, max = 3)
  @JsonProperty("phone_numbers")
  List<PhoneNumber> phoneNumbers;

  @JsonProperty("last_updated")
  @Schema(example = "2020-07-20T13:49:14Z")
  Instant lastUpdated;

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class Administrator {
    @NotBlank
    @Schema(example = "Bob Nelson")
    String name;

    @Schema(example = "bob.nelson@foo.bar")
    @NotBlank
    String email;

    @Valid @NotNull PhoneNumber phone;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class PhoneNumber {
    @Schema(example = "Main")
    String label;

    @Schema(example = "123-456-7890")
    @NotBlank
    String number;

    @Schema(example = "9999")
    String extension;
  }
}
