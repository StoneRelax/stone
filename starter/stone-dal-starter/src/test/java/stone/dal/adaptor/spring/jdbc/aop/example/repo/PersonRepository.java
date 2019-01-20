package stone.dal.adaptor.spring.jdbc.aop.example.repo;

import java.util.List;
import stone.dal.adaptor.spring.jdbc.api.StJpaRepository;
import stone.dal.common.models.Person;

public interface PersonRepository extends StJpaRepository<Person, Long> {

  /**
   * select * from user where manager=?
   *
   * @param name If manager
   * @return User list
   */
  List<Person> findByName(String name);

  Person findMale();

}
