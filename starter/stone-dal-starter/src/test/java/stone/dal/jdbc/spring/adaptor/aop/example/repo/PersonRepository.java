package stone.dal.jdbc.spring.adaptor.aop.example.repo;

import java.util.List;
import stone.dal.jdbc.api.StJpaRepository;
import stone.dal.models.Person;

public interface PersonRepository extends StJpaRepository<Person, Long> {

  /**
   * select * from user where manager=?
   *
   * @param name If manager
   * @return User list
   */
  List<Person> findByName(String name);

}
