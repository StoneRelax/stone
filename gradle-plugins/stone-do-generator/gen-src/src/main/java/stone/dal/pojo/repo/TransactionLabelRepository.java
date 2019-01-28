package stone.dal.pojo.repo;

import stone.dal.adaptor.spring.jdbc.api.StJpaRepository;
import stone.dal.pojo.jpa.TransactionLabel;

public interface TransactionLabelRepository extends StJpaRepository<TransactionLabel, Long> {


}