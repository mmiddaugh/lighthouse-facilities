package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import gov.va.api.lighthouse.facilities.api.v1.FacilityV1;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class FacilitiesResponseV1 {
  @Valid List<FacilityV1> data;

  @Valid @NotNull PageLinks links;

  @Valid @NotNull FacilitiesMetadata meta;

  @Value
  @Builder
  @Schema
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class Distance {
    @NotNull String id;

    @NotNull BigDecimal distance;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class FacilitiesMetadata {
    @Valid @NotNull Pagination pagination;

    @Valid @NotNull List<Distance> distances;
  }
}
