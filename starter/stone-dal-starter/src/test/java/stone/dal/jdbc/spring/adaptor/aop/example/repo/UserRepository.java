package stone.dal.jdbc.spring.adaptor.aop.example.repo;

import java.util.List;
import stone.dal.jdbc.api.StJpaRepository;
import stone.dal.models.User;

public interface UserRepository extends StJpaRepository<User, Long> {

  /**
   * select * from user where manager=?
   *
   * @param manager If manager
   * @return User list
   */
  List<User> findByManager(boolean manager);

}
