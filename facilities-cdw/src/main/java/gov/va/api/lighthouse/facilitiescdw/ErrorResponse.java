package gov.va.api.lighthouse.facilitiescdw;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** The error response is the payload returned to the caller should a failure occur. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "error")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonDeserialize(builder = ErrorResponse.ErrorResponseBuilder.class)
public final class ErrorResponse {
  long timestamp;

  String type;

  String message;

  /** Create a new error response based on the given exception. */
  public static ErrorResponse of(@NonNull Exception e) {
    return ErrorResponse.builder()
        .timestamp(Instant.now().toEpochMilli())
        .type(e.getClass().getSimpleName())
        .message(e.getMessage())
        .build();
  }
}