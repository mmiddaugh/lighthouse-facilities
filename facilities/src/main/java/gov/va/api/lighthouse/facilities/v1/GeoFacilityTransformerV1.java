package gov.va.api.lighthouse.facilities.v1;

import static org.apache.commons.lang3.StringUtils.isBlank;

import gov.va.api.lighthouse.facilities.api.v1.FacilityV1;
import gov.va.api.lighthouse.facilities.api.v1.GeoFacilityV1;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;

@Builder
public final class GeoFacilityTransformerV1 {
  @NonNull private final FacilityV1 facility;

  private GeoFacilityV1.Geometry geometry() {
    FacilityV1.FacilityAttributes attr = facility.attributes();
    if (attr == null || (attr.longitude() == null && attr.latitude() == null)) {
      return null;
    }
    return GeoFacilityV1.Geometry.builder()
        .type(GeoFacilityV1.GeometryType.Point)
        .coordinates(List.of(attr.longitude(), attr.latitude()))
        .build();
  }

  private String id() {
    return facility.id();
  }

  private GeoFacilityV1.Properties properties() {
    if (isBlank(id())) {
      return null;
    }
    FacilityV1.FacilityAttributes attr = facility.attributes();
    return GeoFacilityV1.Properties.builder()
        .id(id())
        .name(attr.name())
        .facilityType(attr.facilityType())
        .classification(attr.classification())
        .website(attr.website())
        .address(attr.address())
        .phone(attr.phone())
        .hours(attr.hours())
        .operationalHoursSpecialInstructions(attr.operationalHoursSpecialInstructions())
        .services(attr.services())
        .satisfaction(attr.satisfaction())
        .waitTimes(attr.waitTimes())
        .mobile(attr.mobile())
        .activeStatus(attr.activeStatus())
        .operatingStatus(attr.operatingStatus())
        .detailedServices(attr.detailedServices())
        .visn(attr.visn())
        .build();
  }

  public GeoFacilityV1 toGeoFacility() {
    if (isBlank(id())) {
      return null;
    }
    return GeoFacilityV1.builder()
        .type(GeoFacilityV1.Type.Feature)
        .geometry(geometry())
        .properties(properties())
        .build();
  }
}
