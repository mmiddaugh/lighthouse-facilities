package gov.va.api.lighthouse.facilities;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static gov.va.api.lighthouse.facilities.collector.FacilitiesCollector.loadWebsites;

import gov.va.api.lighthouse.facilities.api.v0.cms.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v0.cms.DetailedService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.DataBinder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@Validated
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CmsOverlayController {
  public static final String CMS_OVERLAY_SERVICE_NAME_COVID_19 = "COVID-19 vaccines";

  private static final String COVID_CSV_WEBSITES_RESOURCE_NAME = "COVID-19-Facility-URLs.csv";

  private final FacilityRepository repository;

  @InitBinder
  void initDirectFieldAccess(DataBinder dataBinder) {
    dataBinder.initDirectFieldAccess();
  }

  @PostMapping(
      value = "/v0/facilities/{id}/cms-overlay",
      produces = "application/json",
      consumes = "application/json")
  @SneakyThrows
  ResponseEntity<Void> saveOverlay(
      @PathVariable("id") String id, @Valid @RequestBody CmsOverlay overlay) {
    Optional<FacilityEntity> existingEntity =
        repository.findById(FacilityEntity.Pk.fromIdString(id));
    if (existingEntity.isEmpty()) {
      log.info("Received Unknown Facility ID ({}) for CMS Overlay", sanitize(id));
      return ResponseEntity.accepted().build();
    }

    FacilityEntity entity = existingEntity.get();

    if (overlay.operatingStatus() != null) {
      entity.cmsOperatingStatus(
          FacilitiesJacksonConfig.createMapper().writeValueAsString(overlay.operatingStatus()));
    }

    if (overlay.detailedServices() != null) {
      // Compile the set of overlay services to be added
      Set<String> detailedServices = new HashSet<>();
      List<DetailedService> activeServices = new ArrayList<>();

      for (DetailedService service : overlay.detailedServices()) {

        // Since the covid 19 service name doesn't match our enum, we need to update and verify??
        if (service.active()) {

          // Set facility description to null until further notice
          service.descriptionFacility(null);

          if (service.name().equals(CMS_OVERLAY_SERVICE_NAME_COVID_19)) {
            detailedServices.add("Covid19Vaccine");

            Map<String, String> websites = loadWebsites(COVID_CSV_WEBSITES_RESOURCE_NAME);
            service.path(websites.get(id));
          } else {
            detailedServices.add(service.name());
          }
          activeServices.add(service);
        }
      }
      entity.overlayServices(detailedServices);

      // Save the full payload if there are any active services
      entity.cmsServices(
          activeServices.size() > 0
              ? FacilitiesJacksonConfig.createMapper().writeValueAsString(activeServices)
              : null);
    }

    repository.save(entity);
    return ResponseEntity.ok().build();
  }
}
