package gov.va.api.lighthouse.facilities.v1;

import gov.va.api.lighthouse.facilities.api.v1.Facility;
import gov.va.api.lighthouse.facilities.api.v1.cms.CmsOverlay;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class GraveyardResponseV1 {
  @Builder.Default List<Item> facilities = new ArrayList<>();

  @Value
  @Builder
  static final class Item {
    private Facility facility;

    private CmsOverlay cmsOverlay;

    private Set<String> overlayServices;

    private Instant missing;

    private Instant lastUpdated;
  }
}
