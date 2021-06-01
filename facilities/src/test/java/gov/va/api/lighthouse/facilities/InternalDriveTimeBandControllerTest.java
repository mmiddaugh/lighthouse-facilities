package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.facilities.api.v0.pssg.BandResult;
import gov.va.api.lighthouse.facilities.api.v0.pssg.PathEncoder;
import gov.va.api.lighthouse.facilities.api.v0.pssg.PssgDriveTimeBand;
import gov.va.api.lighthouse.facilities.api.v0.pssg.PssgResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InternalDriveTimeBandControllerTest {
  @Mock DriveTimeBandRepository repo;

  InternalDriveTimeBandController controller() {
    return InternalDriveTimeBandController.builder().repository(repo).build();
  }

  @Test
  void getAllBands() {
    when(repo.findAllIds())
        .thenReturn(
            List.of(
                DriveTimeBandEntity.Pk.fromName("a-10-20"),
                DriveTimeBandEntity.Pk.fromName("a-20-30"),
                DriveTimeBandEntity.Pk.fromName("b-10-20")));
    assertThat(controller().driveTimeBandIds())
        .containsExactlyInAnyOrder("a-10-20", "a-20-30", "b-10-20");
  }

  @Test
  void getBandByNameReturnsKnownBand() {
    var e = Entities.diamond("a-1-2", 100);
    when(repo.findById(e.id())).thenReturn(Optional.of(e));
    assertThat(controller().band("a-1-2"))
        .isEqualTo(
            BandResult.builder()
                .stationNumber(e.id().stationNumber())
                .fromMinutes(e.id().fromMinutes())
                .toMinutes(e.id().toMinutes())
                .minLatitude(e.minLatitude())
                .minLongitude(e.minLongitude())
                .maxLatitude(e.maxLatitude())
                .maxLongitude(e.maxLongitude())
                .monthYear(e.monthYear())
                .band(e.band())
                .build());
  }

  @Test
  void getBandByNameThrowsExceptionForUnknownBand() {
    when(repo.findById(DriveTimeBandEntity.Pk.fromName("a-1-2"))).thenReturn(Optional.empty());
    assertThatExceptionOfType(ApiExceptions.NotFound.class)
        .isThrownBy(() -> controller().band("a-1-2"));
  }

  @Test
  void updateBandCreatesNewRecord() {
    var existingA12 = Entities.diamond("a-1-2", 900); // exists
    var existingA23 = Entities.diamond("a-2-3", 800); // exists
    var a12 = Entities.diamond("a-1-2", 100); // update
    var a23 = Entities.diamond("a-2-3", 200); // update
    var a34 = Entities.diamond("a-3-4", 300); // create

    when(repo.findById(a12.id())).thenReturn(Optional.of(existingA12));
    when(repo.findById(a23.id())).thenReturn(Optional.of(existingA23));
    when(repo.findById(a34.id())).thenReturn(Optional.empty());

    controller()
        .update(
            PssgResponse.builder()
                .features(
                    List.of(
                        Entities.diamondBand("a-1-2", 100),
                        Entities.diamondBand("a-2-3", 200),
                        Entities.diamondBand("a-3-4", 300)))
                .build());

    verify(repo).save(a12);
    verify(repo).save(a23);
    verify(repo).save(a34);
  }

  static final class Entities {
    static DriveTimeBandEntity diamond(String name, int offset) {
      return DriveTimeBandEntity.builder()
          .id(DriveTimeBandEntity.Pk.fromName(name))
          .maxLongitude(offset + 1)
          .maxLatitude(offset + 2)
          .minLongitude(offset - 1)
          .minLatitude(offset - 2)
          .monthYear("MAR2021")
          .band(PathEncoder.create().encodeToBase64(diamondBand(name, offset)))
          .build();
    }

    static PssgDriveTimeBand diamondBand(String name, int offset) {
      var pk = DriveTimeBandEntity.Pk.fromName(name);
      List<List<Double>> ring1 = PssgDriveTimeBand.newRing(4);
      // Diamond around offset,offset
      ring1.add(PssgDriveTimeBand.coord(offset, offset + 2));
      ring1.add(PssgDriveTimeBand.coord(offset + 1, offset));
      ring1.add(PssgDriveTimeBand.coord(offset, offset - 2));
      ring1.add(PssgDriveTimeBand.coord(offset - 1, offset));
      List<List<List<Double>>> rings = PssgDriveTimeBand.newListOfRings();
      rings.add(ring1);
      return PssgDriveTimeBand.builder()
          .attributes(
              PssgDriveTimeBand.Attributes.builder()
                  .stationNumber(pk.stationNumber())
                  .fromBreak(pk.fromMinutes())
                  .toBreak(pk.toMinutes())
                  .monthYear("MAR2021")
                  .build())
          .geometry(PssgDriveTimeBand.Geometry.builder().rings(rings).build())
          .build();
    }
  }
}
