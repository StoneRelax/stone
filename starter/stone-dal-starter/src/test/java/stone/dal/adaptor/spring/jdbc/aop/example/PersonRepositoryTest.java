package stone.dal.adaptor.spring.jdbc.aop.example;

import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import stone.dal.adaptor.spring.jdbc.aop.example.repo.PersonRepository;
import stone.dal.adaptor.spring.jdbc.api.StJdbcTemplate;
import stone.dal.adaptor.spring.jdbc.spring.adaptor.app.SpringJdbcAdaptorTestApplication;
import stone.dal.common.models.Person;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringJdbcAdaptorTestApplication.class)
@EnableTransactionManagement
public class PersonRepositoryTest {

  @Autowired
  private PersonRepository personRepository;

  @Autowired
  private StJdbcTemplate jdbcTemplate;

  @Before
  public void setup() {
    jdbcTemplate.exec("delete from person");
  }

  @Autowired
  private PersonService personService;

  @Test
  public void testFindOneById() {
    Person user = new Person();
    user.setUuid(1002l);
    user.setName("Xie Feng");
    personRepository.create(user);
    Assert.assertEquals("Xie Feng", personRepository.findByPk(1002l).getName());
  }

  @Test
  public void testFindAll() {
    Person user = new Person();
    user.setUuid(1002l);
    user.setName("Xie Feng");
    personRepository.create(user);
    Assert.assertEquals("Xie Feng", personRepository.findAll().iterator().next().getName());
  }

  @Test
  public void testFindUserByManager() {
    Person user = new Person();
    user.setUuid(1002l);
    user.setName("Xie Feng");
    personRepository.create(user);

    List<Person> users = personRepository.findByName("Xie Feng");
    Assert.assertEquals("Xie Feng", users.get(0).getName());

    Person person = personRepository.findMale();
    Assert.assertEquals("MaleA", person.getName());
  }

  @Test
  public void testRollback() {
    try {
      personService.testTx(10003l, "Stone", true);
    } catch (Exception ex) {
    }
    List<Person> usersStone = personRepository.findByName("Stone");
    Assert.assertNull(usersStone);
  }

  @Test
  public void testCommit() {
    try {
      personService.testTx(10003l, "Stone", false);
    } catch (Exception ex) {
    }
    List<Person> usersStone = personRepository.findByName("Stone");
    Assert.assertEquals("Stone", usersStone.get(0).getName());
  }

}
