package gov.va.api.lighthouse.facilities;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;

import gov.va.api.lighthouse.facilities.FacilityEntity.Pk;
import gov.va.api.lighthouse.facilities.api.cms.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.urgentcontact.UrgentContact;
import gov.va.api.lighthouse.facilities.api.v0.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.DataBinder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// @Slf4j
@Validated
@RestController
@Builder
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UrgentContactController {
  private static final ObjectMapper MAPPER = FacilitiesJacksonConfig.createMapper();

  private final UrgentContactRepository repository;

  @SneakyThrows
  private static UrgentContact urgentContact(UrgentContactEntity entity) {
    return MAPPER.readValue(entity.payload(), UrgentContact.class);
  }

  /** Save urgent contact. */
  @SneakyThrows
  @PostMapping(
      value = "/v0/urgent-contact",
      produces = "application/json",
      consumes = "application/json")
  public void save(@Valid @RequestBody UrgentContact contact) {
    String id = contact.id();
    UrgentContactEntity entity =
        repository.findById(id).orElse(UrgentContactEntity.builder().id(id).build());

    UrgentContact newContact = contact.toBuilder().lastUpdated(Instant.now()).build();
    entity.facilityId(UrgentContactEntity.FacilityId.fromIdString(contact.facilityId()));
    entity.payload(FacilitiesJacksonConfig.createMapper().writeValueAsString(newContact));

    repository.save(entity);
  }

  /** Read urgent contact. */
  @GetMapping(value = "/v0/urgent-contact/{id}", produces = "application/json")
  public UrgentContact read(@PathVariable("id") String id) {
    Optional<UrgentContactEntity> opt = repository.findById(id);
    if (opt.isEmpty()) {
      throw new ExceptionsV0.NotFound(id);
    }
    return urgentContact(opt.get());
  }

  /** Search urgent contacts. */
  @GetMapping(value = "/v0/urgent-contact", produces = "application/json")
  public List<UrgentContact> search(@RequestParam(value = "facility_id") String facilityId) {
    List<UrgentContactEntity> entities =
        repository.findByFacilityId(UrgentContactEntity.FacilityId.fromIdString(facilityId));
    return entities.stream().map(e -> urgentContact(e)).collect(toList());
  }
}
