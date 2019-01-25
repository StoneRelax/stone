package stone.dal.adaptor.spring.jdbc.aop.example.repo;

import org.springframework.beans.factory.annotation.Autowired;
import stone.dal.adaptor.spring.jdbc.api.StJdbcTemplate;
import stone.dal.adaptor.spring.jdbc.api.StJpaRepository;
import stone.dal.adaptor.spring.jdbc.api.meta.SqlCondition;
import stone.dal.common.models.Person;

public abstract class PersonRepositoryImpl implements PersonRepository {

  @Autowired
  private StJdbcTemplate jdbcTemplate;

  @Autowired
  private StJpaRepository<Person, Long> jpaRepository;

  @Override
  public Person findMale() {
    jdbcTemplate.query(SqlCondition.create(Person.class).eq("name", "Xie"));
    Person person = new Person();
    person.setName("MaleA");
    return person;
  }

  public void testTxRollback(Long uuid, String name) {
    Person userStone = new Person();
    userStone.setUuid(uuid);
    userStone.setName(name);
    jpaRepository.create(userStone);
    throw new RuntimeException("Some problem happens");
  }

  @Override
  public void textTxCommit(Long uuid, String name) {
    Person userStone = new Person();
    userStone.setUuid(uuid);
    userStone.setName(name);
    jpaRepository.create(userStone);
  }
}
