package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.v0.JacksonSerializersV0;
import gov.va.api.lighthouse.facilities.v1.JacksonSerializersV1;
import java.io.InputStream;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FacilitiesJacksonConfig {
  public static ObjectMapper createMapper() {
    return new FacilitiesJacksonConfig().objectMapper();
  }

  /** Used for the overlay version of splitting the piped operational_hours_special_instructions. */
  public static ObjectMapper createMapperV1() {
    return new FacilitiesJacksonConfig().objectMapperV1();
  }

  /** Mask away checked exception so this Jackson can be used in streams. */
  @SneakyThrows
  static <T> T quietlyMap(ObjectMapper mapper, InputStream json, Class<T> type) {
    return mapper.readValue(json, type);
  }

  /** Mask away checked exception so this Jackson can be used in streams. */
  @SneakyThrows
  static <T> T quietlyMap(ObjectMapper mapper, String json, Class<T> type) {
    return mapper.readValue(json, type);
  }

  /** Mask away checked exception so this Jackson can be used in streams. */
  @SneakyThrows
  public static String quietlyWriteValueAsString(ObjectMapper mapper, Object obj) {
    return mapper.writeValueAsString(obj);
  }

  @Bean
  public ObjectMapper objectMapper() {
    return JacksonConfig.createMapper().registerModule(JacksonSerializersV0.serializersV0());
  }

  /** Used for the overlay version of splitting the piped operational_hours_special_instructions. */
  @Bean
  public ObjectMapper objectMapperV1() {
    return JacksonConfig.createMapper().registerModule(JacksonSerializersV1.serializersV0());
  }
}
