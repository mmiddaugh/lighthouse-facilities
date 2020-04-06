package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class BingResponse {
  @Builder.Default List<ResourceSet> resourceSets = new ArrayList<>();

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static final class BingResponseBuilder {}

  @Value
  @Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static final class Point {
    @Builder.Default List<BigDecimal> coordinates = new ArrayList<>();

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class PointBuilder {}
  }

  @Value
  @Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static final class Resource {
    Point point;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class ResourceBuilder {}
  }

  @Value
  @Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static final class ResourceSet {
    @Builder.Default List<Resource> resources = new ArrayList<>();

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class ResourceSetBuilder {}
  }
}