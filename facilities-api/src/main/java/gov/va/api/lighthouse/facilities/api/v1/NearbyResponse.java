package gov.va.api.lighthouse.facilities.api.v1;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder({"data", "meta"})
public class NearbyResponse {
  @Valid @NotNull List<NearbyResponse.Nearby> data;

  @Schema(nullable = true)
  NearbyResponse.Meta meta;

  public enum Type {
    @JsonProperty("nearby_facility")
    NearbyFacility
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @Schema(nullable = true)
  public static final class NearbyAttributes {
    @NotNull
    @Schema(example = "10")
    @JsonProperty("min_time")
    Integer minTime;

    @NotNull
    @Schema(example = "20")
    @JsonProperty("max_time")
    Integer maxTime;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @Schema(
      description = "JSON API-compliant object containing metadata about this response",
      nullable = true)
  public static final class Meta {
    @Schema(example = "APR2021", nullable = true)
    @JsonProperty("band_version")
    String bandVersion;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @Schema(
      description = "JSON API-compliant object describing a nearby VA facility",
      nullable = true)
  public static final class Nearby {
    @Schema(example = "vha_688")
    @NotNull
    String id;

    @Schema(example = "va_health_facility")
    @NotNull
    NearbyResponse.Type type;

    @Valid @NotNull NearbyResponse.NearbyAttributes attributes;
  }
}
