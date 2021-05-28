package gov.va.api.lighthouse.facilities.v1;

import gov.va.api.health.autoconfig.logging.Loggable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Loggable
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public interface FacilityGraveyardRepository
    extends CrudRepository<FacilityGraveyardEntity, FacilityEntity.Pk> {}
