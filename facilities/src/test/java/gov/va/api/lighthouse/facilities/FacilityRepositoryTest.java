package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.v0.FacilityRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class FacilityRepositoryTest {
  @Autowired FacilityRepository repository;

  private FacilityEntity facilityEntity(String stnNumber, Instant lastUpdated) {
    return FacilityEntity.builder()
        .id(FacilityEntity.Pk.of(FacilityEntity.Type.vha, stnNumber))
        .facility("vha_" + stnNumber)
        .lastUpdated(lastUpdated)
        .build();
  }

  @Test
  void findAllIds() {
    List<FacilityEntity.Pk> expected = new ArrayList<>();
    FacilityEntity entity;
    var now = Instant.now();
    for (int i = 0; i < 5; i++) {
      entity = facilityEntity("" + i, now);
      expected.add(entity.id());
      repository.save(entity);
    }
    assertThat(repository.findAllIds()).containsExactlyElementsOf(expected);
  }

  @Test
  void lastUpdated() {
    var aLongTimeAgo = Instant.parse("2020-01-20T02:20:00Z");
    repository.save(facilityEntity("1", aLongTimeAgo));
    assertThat(repository.findLastUpdated()).isEqualTo(aLongTimeAgo);
    var now = Instant.ofEpochMilli(System.currentTimeMillis());
    repository.save(facilityEntity("2", now));
    assertThat(repository.findLastUpdated()).isEqualTo(now);
  }
}
