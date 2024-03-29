package gov.va.api.lighthouse.facilitiesmockservices;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Options for the mock server. */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("mock-services")
@Data
@Accessors(fluent = false)
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MockServicesOptions {
  /** The port that mock server will be started on. */
  int port;
}
