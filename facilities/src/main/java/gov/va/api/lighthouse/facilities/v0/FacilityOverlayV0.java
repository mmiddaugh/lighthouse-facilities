package gov.va.api.lighthouse.facilities.v0;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.HasFacilityPayload;
import gov.va.api.lighthouse.facilities.api.v0.FacilityV0;
import gov.va.api.lighthouse.facilities.api.v0.FacilityV0.ActiveStatus;
import gov.va.api.lighthouse.facilities.api.v0.FacilityV0.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.v0.FacilityV0.OperatingStatusCode;
import gov.va.api.lighthouse.facilities.api.v0.cms.DetailedService;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Builder
@Value
@Slf4j
public class FacilityOverlay implements Function<HasFacilityPayload, FacilityV0> {
  @NonNull ObjectMapper mapper;

  private static void applyCmsOverlayOperatingStatus(
      FacilityV0 facility, FacilityV0.OperatingStatus operatingStatus) {
    if (operatingStatus == null) {
      log.warn("CMS Overlay for facility {} is missing operating status", facility.id());
    } else {
      facility.attributes().operatingStatus(operatingStatus);
      if (operatingStatus.code() == OperatingStatusCode.CLOSED) {
        facility.attributes().activeStatus(ActiveStatus.T);
      } else {
        facility.attributes().activeStatus(ActiveStatus.A);
      }
    }
  }

  private static void applyCmsOverlayServices(FacilityV0 facility, Set<String> overlayServices) {
    if (overlayServices == null) {
      log.warn("CMS Overlay for facility {} is missing CMS Services", facility.id());
    } else {
      boolean needToSort = false;
      for (String overlayService : overlayServices) {
        if ("Covid19Vaccine".equals(overlayService)) {
          if (facility.attributes().services().health() != null) {
            facility.attributes().services().health().add(FacilityV0.HealthService.Covid19Vaccine);
          } else {
            facility
                .attributes()
                .services()
                .health(List.of(FacilityV0.HealthService.Covid19Vaccine));
          }
          needToSort = true;
          break;
        }
      }
      // re-sort the health services list with the newly added field(s)
      if (needToSort && facility.attributes().services().health().size() > 1) {
        Collections.sort(
            facility.attributes().services().health(),
            (left, right) -> left.name().compareToIgnoreCase(right.name()));
      }
    }
  }

  private static void applyDetailedServices(
      FacilityV0 facility, List<DetailedService> detailedServices) {
    if (detailedServices == null) {
      log.warn("CMS Overlay for facility {} is missing Detailed CMS Services", facility.id());
    } else {
      facility.attributes().detailedServices(detailedServices);
    }
  }

  private static OperatingStatus determineOperatingStatusFromActiveStatus(
      ActiveStatus activeStatus) {
    if (activeStatus == ActiveStatus.T) {
      return OperatingStatus.builder().code(OperatingStatusCode.CLOSED).build();
    }
    return OperatingStatus.builder().code(OperatingStatusCode.NORMAL).build();
  }

  @Override
  @SneakyThrows
  public FacilityV0 apply(HasFacilityPayload entity) {
    FacilityV0 facility = mapper.readValue(entity.facility(), FacilityV0.class);
    if (entity.cmsOperatingStatus() != null) {
      applyCmsOverlayOperatingStatus(
          facility,
          mapper.readValue(entity.cmsOperatingStatus(), FacilityV0.OperatingStatus.class));
    }
    if (facility.attributes().operatingStatus() == null) {
      facility
          .attributes()
          .operatingStatus(
              determineOperatingStatusFromActiveStatus(facility.attributes().activeStatus()));
    }
    if (entity.overlayServices() != null) {
      applyCmsOverlayServices(facility, entity.overlayServices());
    }
    if (entity.cmsServices() != null) {
      applyDetailedServices(
          facility, List.of(mapper.readValue(entity.cmsServices(), DetailedService[].class)));
    }
    return facility;
  }
}
