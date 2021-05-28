package gov.va.api.lighthouse.facilities.api.v1;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class GeoFacilityReadResponse {
  @NotNull GeoFacility.Type type;

  @Valid @NotNull GeoFacility.Geometry geometry;

  @Valid @NotNull GeoFacility.Properties properties;

  /** Create GeoFacilityReadResponse with the same type, geometry, and properties. */
  public static GeoFacilityReadResponse of(@NonNull GeoFacility facility) {
    return GeoFacilityReadResponse.builder()
        .type(facility.type())
        .geometry(facility.geometry())
        .properties(facility.properties())
        .build();
  }
}
