package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.FacilitiesJacksonConfig.quietlyMap;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacility;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.SneakyThrows;

public class FacilitySamples {
  private final Map<String, Facility> facilities;

  @SneakyThrows
  @Builder
  public FacilitySamples(List<String> resources) {
    var mapper = FacilitiesJacksonConfig.createMapper();
    facilities =
        resources.stream()
            .map(r -> getClass().getResourceAsStream(r))
            .map(in -> quietlyMap(mapper, in, FacilityReadResponse.class))
            .map(FacilityReadResponse::facility)
            .collect(Collectors.toMap(Facility::id, Function.identity()));
  }

  public static FacilitySamples defaultSamples() {
    return FacilitySamples.builder()
        .resources(List.of("/vha_691GB.json", "/vha_740GA.json", "/vha_757.json"))
        .build();
  }

  Facility facility(String id) {
    var f = facilities.get(id);
    assertThat(f).describedAs(id).isNotNull();
    return f;
  }

  FacilityEntity facilityEntity(String id) {
    return InternalManagementController.populate(
        FacilityEntity.builder().id(FacilityEntity.Pk.fromIdString(id)).build(), facility(id));
  }

  GeoFacility geoFacility(String id) {
    return GeoFacilityTransformer.builder().facility(facility(id)).build().toGeoFacility();
  }
}