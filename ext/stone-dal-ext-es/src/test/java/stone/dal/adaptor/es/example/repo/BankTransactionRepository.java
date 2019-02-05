package stone.dal.adaptor.es.example.repo;

import stone.dal.common.models.BankTransaction;
import stone.dal.common.models.Person;
import stone.dal.jdbc.api.StJpaRepository;

public interface BankTransactionRepository extends StJpaRepository<BankTransaction, Long> {
}
