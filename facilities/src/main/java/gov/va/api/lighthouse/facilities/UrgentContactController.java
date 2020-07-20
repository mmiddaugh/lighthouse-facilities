package gov.va.api.lighthouse.facilities;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.urgentcontact.UrgentContact;
import gov.va.api.lighthouse.facilities.api.urgentcontact.UrgentContactsResponse;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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

  /** Delete urgent contact by ID. */
  @DeleteMapping(value = "/internal/management/urgent-contact/{id}")
  public ResponseEntity<Void> deleteUrgentContactById(@PathVariable("id") String id) {
    Optional<UrgentContactEntity> opt = repository.findById(id);
    if (opt.isEmpty()) {
      log.info("Ignoring delete for unknown urgent contact {}.", sanitize(id));
      return ResponseEntity.accepted().build();
    }
    log.info("Deleting urgent contact {}.", sanitize(id));
    repository.delete(opt.get());
    return ResponseEntity.ok().build();
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

  /** Search urgent contacts. */
  @GetMapping(value = "/v0/urgent-contact", produces = "application/json")
  public UrgentContactsResponse search(@RequestParam(value = "facility_id") String facilityId) {
    List<UrgentContactEntity> entities =
        repository.findByFacilityId(UrgentContactEntity.FacilityId.fromIdString(facilityId));
    return UrgentContactsResponse.builder()
        .urgentContacts(entities.stream().map(e -> urgentContact(e)).collect(toList()))
        .build();
  }
}
