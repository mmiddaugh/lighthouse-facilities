package gov.va.api.lighthouse.facilities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Entity
@Builder
@Table(name = "facility_urgent_contact", schema = "app")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UrgentContactEntity {
  @Id @EqualsAndHashCode.Include private String id;

  @Version private Integer version;

  @Embedded private FacilityId facilityId;

  @Lob
  @Column
  @Basic(fetch = FetchType.EAGER)
  private String payload;

  @Data
  @NoArgsConstructor(access = AccessLevel.PUBLIC)
  @AllArgsConstructor(staticName = "of")
  public static final class FacilityId implements Serializable {
    @Enumerated(EnumType.STRING)
    @Column(name = "facility_type")
    private FacilityEntity.Type facilityType;

    @Column(name = "station_number")
    private String stationNumber;

    /** Create from the {type}_{id} style used in the Facilities API ID. */
    public static FacilityId fromIdString(@NonNull String typeAndStationNumber) {
      FacilityEntity.Pk pk = FacilityEntity.Pk.fromIdString(typeAndStationNumber);
      return of(pk.type(), pk.stationNumber());
    }
  }
}
