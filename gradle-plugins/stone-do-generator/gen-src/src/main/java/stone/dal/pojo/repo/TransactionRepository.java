package stone.dal.pojo.repo;

import stone.dal.adaptor.spring.jdbc.api.StJpaRepository;
import stone.dal.pojo.jpa.Transaction;

public interface TransactionRepository extends StJpaRepository<Transaction, Long> {


}