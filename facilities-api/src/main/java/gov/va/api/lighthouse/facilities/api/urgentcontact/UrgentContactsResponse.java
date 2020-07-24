package gov.va.api.lighthouse.facilities.api.urgentcontact;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Schema(description = "Urgent contact phone numbers for a clinic")
public final class UrgentContactsResponse {
  @Builder.Default List<UrgentContact> urgentContacts = new ArrayList<>();
}
