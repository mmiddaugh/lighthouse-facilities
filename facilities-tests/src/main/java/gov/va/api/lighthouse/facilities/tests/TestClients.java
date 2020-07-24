package gov.va.api.lighthouse.facilities.tests;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.sentinel.BasicTestClient;
import gov.va.api.health.sentinel.TestClient;
import lombok.experimental.UtilityClass;

/* Test clients for interacting with services in a SystemDefinition. */
@UtilityClass
public class TestClients {
  static TestClient collector() {
    return BasicTestClient.builder()
        .service(SystemDefinitions.systemDefinition().collector())
        .mapper(JacksonConfig::createMapper)
        .build();
  }

  static TestClient facilities() {
    return BasicTestClient.builder()
        .service(SystemDefinitions.systemDefinition().facilities())
        .mapper(JacksonConfig::createMapper)
        .contentType("application/json")
        .build();
  }

  static TestClient facilitiesManagement() {
    return BasicTestClient.builder()
        .service(SystemDefinitions.systemDefinition().facilitiesManagement())
        .mapper(JacksonConfig::createMapper)
        .contentType("application/json")
        .build();
  }
}
