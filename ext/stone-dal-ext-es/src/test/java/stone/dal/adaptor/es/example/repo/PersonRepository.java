package stone.dal.adaptor.es.example.repo;

import stone.dal.common.models.Person;
import stone.dal.jdbc.api.StJpaRepository;

import java.util.List;

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
