package stone.dal.adaptor.spring.jdbc.aop.example.repo;

import java.util.List;
import stone.dal.common.models.Person;
import stone.dal.jdbc.api.StJpaRepository;

public interface PersonRepository extends StJpaRepository<Person, Long> {

  /**
   * select * from user where manager=?
   *
   * @param name If manager
   * @return User list
   */
  List<Person> findByName(String name);

  void testTxRollback(Long uuid, String name);

  void textTxCommit(Long uuid, String name);

  Person findMale();

}
