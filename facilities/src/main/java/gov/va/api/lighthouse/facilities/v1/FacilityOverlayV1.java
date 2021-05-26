package gov.va.api.lighthouse.facilities.v1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.HasFacilityPayload;
import gov.va.api.lighthouse.facilities.api.cms.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.FacilityV1;
import gov.va.api.lighthouse.facilities.api.v1.FacilityV1.ActiveStatus;
import gov.va.api.lighthouse.facilities.api.v1.FacilityV1.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.v1.FacilityV1.OperatingStatusCode;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Builder
@Value
@Slf4j
public class FacilityOverlayV1 implements Function<HasFacilityPayload, FacilityV1> {
  @NonNull ObjectMapper mapper;

  private static void applyCmsOverlayOperatingStatus(
          FacilityV1 facility, OperatingStatus operatingStatus) {
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

  private static void applyCmsOverlayServices(FacilityV1 facility, Set<String> overlayServices) {
    if (overlayServices == null) {
      log.warn("CMS Overlay for facility {} is missing CMS Services", facility.id());
    } else {
      boolean needToSort = false;
      for (String overlayService : overlayServices) {
        if ("Covid19Vaccine".equals(overlayService)) {
          if (facility.attributes().services().health() != null) {
            facility.attributes().services().health().add(FacilityV1.HealthService.Covid19Vaccine);
          } else {
            facility.attributes().services().health(List.of(FacilityV1.HealthService.Covid19Vaccine));
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
          FacilityV1 facility, List<DetailedService> detailedServices) {
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
  public FacilityV1 apply(HasFacilityPayload entity) {
    ObjectMapper objectMapper = new ObjectMapper();

    JsonNode jsonNode = objectMapper.readTree(entity.facility());
    JsonNode attNode = jsonNode.get("attributes");
    String specialInstructions = attNode.get("operational_hours_special_instructions").asText();

    System.out.println(objectMapper.writeValueAsString(specialInstructions.split("\\s*\\|\\s*")));
    String s = entity
            .facility()
            .replace(
                    "\"More hours are available for some services. To learn more, call our main phone number. | If you need to talk to someone or get advice right away, call the Vet Center anytime at 1-877-WAR-VETS (1-877-927-8387).\"",
                    objectMapper.writeValueAsString(specialInstructions.split("\\s*\\|\\s*")));
    System.out.println(s);
    FacilityV1 facility = mapper.readValue(s, FacilityV1.class);
    if (entity.cmsOperatingStatus() != null) {
      applyCmsOverlayOperatingStatus(
          facility, mapper.readValue(entity.cmsOperatingStatus(), OperatingStatus.class));
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
