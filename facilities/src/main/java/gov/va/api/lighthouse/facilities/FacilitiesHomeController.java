package gov.va.api.lighthouse.facilities;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SuppressWarnings("WeakerAccess")
@Controller
public class FacilitiesHomeController {
  @Autowired
  public FacilitiesHomeController() {}

  /** Load openapi.json content as a string value. */
  @SneakyThrows
  @SuppressWarnings("WeakerAccess")
  @Bean
  public String openapiContent() {
    return "";
  }

  /** OpenAPI Json. */
  @SneakyThrows
  @GetMapping(
      value = {"/", "/docs/v0/api", "/v0/facilities/openapi.json"},
      produces = "application/json")
  @ResponseBody
  public Object openapiJson() {
    return JacksonConfig.createMapper().readValue(openapiContent(), Object.class);
  }
}
