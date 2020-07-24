package gov.va.api.lighthouse.facilitiescollector;

import gov.va.api.health.autoconfig.logging.Loggable;
import java.time.Instant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Loggable
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public interface VastRepository extends CrudRepository<VastEntity, Long> {
  @Query("select max(e.lastUpdated) from #{#entityName} e")
  Instant findLastUpdated();
}
