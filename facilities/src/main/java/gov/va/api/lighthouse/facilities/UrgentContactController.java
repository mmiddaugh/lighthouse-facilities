package gov.va.api.lighthouse.facilities;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import gov.va.api.lighthouse.facilities.FacilityEntity.Pk;
import gov.va.api.lighthouse.facilities.api.cms.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.urgentcontact.UrgentContact;
import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import java.time.Instant;
import java.util.Optional;
import javax.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;

// @Slf4j
@Validated
@RestController
@Builder
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UrgentContactController {
  private static final ObjectMapper MAPPER = FacilitiesJacksonConfig.createMapper();

  private final UrgentContactRepository repository;

  /** Save urgent contact. */
  @SneakyThrows
  @PostMapping(
      value = "/v0/urgent-contact",
      produces = "application/json",
      consumes = "application/json")
  public void saveUrgentContact(@Valid @RequestBody UrgentContact contact) {
    String id = contact.id();
    UrgentContactEntity entity =
        repository.findById(id).orElse(UrgentContactEntity.builder().id(id).build());
    UrgentContact newContact = contact.toBuilder().lastUpdated(Instant.now()).build();
    entity.payload(FacilitiesJacksonConfig.createMapper().writeValueAsString(newContact));
    repository.save(entity);
  }

  /** Read urgent contact. */
  @SneakyThrows
  @GetMapping(value = "/v0/urgent-contact/{id}", produces = "application/json")
  public UrgentContact read(@PathVariable("id") String id) {
    Optional<UrgentContactEntity> opt = repository.findById(id);
    if (opt.isEmpty()) {
      throw new ExceptionsV0.NotFound(id);
    }
    return MAPPER.readValue(opt.get().payload(), UrgentContact.class);
  }
}
