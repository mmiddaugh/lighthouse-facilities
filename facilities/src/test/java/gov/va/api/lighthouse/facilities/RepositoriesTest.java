package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.DriveTimeBandEntity.Pk;
import gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@DataJpaTest
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RepositoriesTest {
  @Autowired FacilityRepository facilityRepository;
  @Autowired DriveTimeBandRepository driveTimeBandRepository;
  @PersistenceContext private EntityManager entityManager;

  @Test
  public void addDriveTimeBandEntities() {
    DriveTimeBandEntity b1230to10 =
        DriveTimeBandEntity.builder()
            .id(Pk.of("123", 0, 10))
            .maxLatitude(80)
            .maxLongitude(50)
            .minLatitude(40)
            .minLongitude(25)
            .band("{garbage}")
            .version(null)
            .build();
    var b1230to10Saved = driveTimeBandRepository.save(b1230to10);
    entityManager.flush();
    var b1230to10Found = driveTimeBandRepository.findById(b1230to10.id()).get();
    assertThat(b1230to10Found).isEqualTo(b1230to10Saved);
  }

  @Test
  public void addFacilityEntities() {
    FacilityEntity f123Vha =
        FacilityEntity.typeSafeBuilder()
            .id(FacilityEntity.Pk.of(FacilityEntity.Type.vha, "123"))
            .latitude(70)
            .longitude(30)
            .zip("90210")
            .state("CA")
            .servicesTypes(Set.of(HealthService.PrimaryCare, HealthService.SpecialtyCare))
            .facility("{garbage}")
            .version(null)
            .build();
    var f123VhaSaved = facilityRepository.save(f123Vha);
    entityManager.flush();
    var f123VhaFound = facilityRepository.findById(f123Vha.id()).get();
    assertThat(f123VhaFound).isEqualTo(f123VhaSaved);
  }
}
