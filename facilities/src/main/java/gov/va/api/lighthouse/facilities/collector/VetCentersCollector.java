package gov.va.api.lighthouse.facilities.collector;

import static java.util.stream.Collectors.toList;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import lombok.Builder;
import lombok.NonNull;
import us.dustinj.timezonemap.TimeZoneMap;

@Builder
@SuppressWarnings("ObjectToString")
final class VetCentersCollector {
  @NonNull final Collection<VastEntity> vastEntities;

  @NonNull final Map<String, String> websites;

  @NonNull private final TimeZoneMap continentalUsTimeZoneMap;

  Collection<Facility> collect() {
    try {
      return vastEntities.stream()
          .filter(Objects::nonNull)
          .filter(vast -> vast.isVetCenter())
          .map(
              vast ->
                  VetCenterTransformer.builder()
                      .vast(vast)
                      .websites(websites)
                      .continentalUsTimeZoneMap(continentalUsTimeZoneMap)
                      .build()
                      .toFacility())
          .filter(Objects::nonNull)
          .collect(toList());
    } catch (Exception e) {
      throw new CollectorExceptions.VetCentersCollectorException(e);
    }
  }
}
