package stone.dal.pojo.repo;

import stone.dal.adaptor.spring.jdbc.api.StJpaRepository;
import stone.dal.pojo.jpa.BankUser;

public interface BankUserRepository extends StJpaRepository<BankUser, Long> {


}