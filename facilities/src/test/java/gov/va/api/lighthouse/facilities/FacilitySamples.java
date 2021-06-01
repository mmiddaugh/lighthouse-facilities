package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.FacilitiesJacksonConfig.quietlyMap;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacility;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.Builder;
import lombok.SneakyThrows;

public class FacilitySamples {
  private final Map<String, Facility> facilities;

  @SneakyThrows
  @Builder
  FacilitySamples(List<String> resources) {
    var mapper = FacilitiesJacksonConfig.createMapper();
    facilities =
        resources.stream()
            .map(r -> getClass().getResourceAsStream(r))
            .map(in -> quietlyMap(mapper, in, FacilityReadResponse.class))
            .map(FacilityReadResponse::facility)
            .collect(toMap(Facility::id, Function.identity()));
  }

  public static FacilitySamples defaultSamples() {
    return FacilitySamples.builder()
        .resources(List.of("/vha_691GB.json", "/vha_740GA.json", "/vha_757.json"))
        .build();
  }

  public Facility facility(String id) {
    var f = facilities.get(id);
    assertThat(f).describedAs(id).isNotNull();
    return f;
  }

  public FacilityEntity facilityEntity(String id) {
    return InternalFacilitiesController.populate(
        FacilityEntity.builder()
            .id(FacilityEntity.Pk.fromIdString(id))
            .lastUpdated(Instant.now())
            .build(),
        facility(id));
  }

  public GeoFacility geoFacility(String id) {
    return GeoFacilityTransformer.builder().facility(facility(id)).build().toGeoFacility();
  }
}
