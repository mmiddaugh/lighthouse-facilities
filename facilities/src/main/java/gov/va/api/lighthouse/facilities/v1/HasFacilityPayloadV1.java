package gov.va.api.lighthouse.facilities.v1;

import java.util.Set;

/** A DTO projection of the FacilityEntity. */
public interface HasFacilityPayloadV1 {
  String cmsOperatingStatus();

  String cmsServices();

  String facility();

  Set<String> overlayServices();
}
