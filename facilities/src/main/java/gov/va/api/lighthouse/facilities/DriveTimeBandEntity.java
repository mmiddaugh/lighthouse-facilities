package gov.va.api.lighthouse.facilities;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Splitter;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "drive_time_band", schema = "app")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DriveTimeBandEntity {
  /**
   * A band is unique to a facility and a drive-time range (or band), e.g. 10-20 minutes. A given
   * facility can have multiple bands around it.
   */
  @EqualsAndHashCode.Include @EmbeddedId private Pk id;

  /** aka y1. */
  @Column(name = "min_latitude")
  private double minLatitude;

  /** aka x1. */
  @Column(name = "min_longitude")
  private double minLongitude;

  /** aka y2. */
  @Column(name = "max_latitude")
  private double maxLatitude;

  /** aka x2. */
  @Column(name = "max_longitude")
  private double maxLongitude;

  @Column(name = "month_year")
  private String monthYear;

  @Lob
  @Basic(fetch = FetchType.LAZY)
  @Column
  private String band;

  @Version private Integer version;

  @Data
  @NoArgsConstructor(access = AccessLevel.PUBLIC)
  @AllArgsConstructor(staticName = "of")
  @Embeddable
  public static final class Pk implements Serializable {
    @Column(name = "station_number", nullable = false)
    private String stationNumber;

    @Column(name = "from_minutes", nullable = false)
    private int fromMinutes;

    @Column(name = "to_minutes", nullable = false)
    private int toMinutes;

    /** Parse a 'name' into a PK. Name format is {stationNumber}-{fromMinutes}-{toMinutes}. */
    static Pk fromName(@NonNull String name) {
      var parts = Splitter.on('-').splitToList(name);
      checkArgument(
          parts.size() == 3,
          "Expected {stationNumber}-{fromMinutes}-{toMinutes}, got \"%s\"",
          name);
      return of(parts.get(0), Integer.parseInt(parts.get(1)), Integer.parseInt(parts.get(2)));
    }

    /** Return a 'name' into a PK. Name format is {stationNumber}-{fromMinutes}-{toMinutes}. */
    public String name() {
      return stationNumber + "-" + fromMinutes + "-" + toMinutes;
    }
  }
}
