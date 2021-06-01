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
public final class GeoFacilityReadResponseV1 {
  @NotNull GeoFacilityV1.Type type;

  @Valid @NotNull GeoFacilityV1.Geometry geometry;

  @Valid @NotNull GeoFacilityV1.Properties properties;

  /** Create GeoFacilityReadResponse with the same type, geometry, and properties. */
  public static GeoFacilityReadResponseV1 of(@NonNull GeoFacilityV1 facility) {
    return GeoFacilityReadResponseV1.builder()
        .type(facility.type())
        .geometry(facility.geometry())
        .properties(facility.properties())
        .build();
  }
}
