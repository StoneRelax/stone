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
public class UserRepositoryTest {

  @Autowired
  private PersonRepository personRepository;

  @Autowired
  private StJdbcTemplate jdbcTemplate;


  @Before
  public void setup() {
    jdbcTemplate.execDcl("delete from person");
  }

  @Autowired
  private TransactionTest transactionTest;

  @Test
  public void testFindUserByManager() {
    Person user = new Person();
    user.setUuid(1002l);
    user.setName("Xie Feng");
    user.setDescription("A very weisuo person");
    personRepository.create(user);

    List<Person> users = personRepository.findByName("Xie Feng");
    Assert.assertEquals("Xie Feng", users.get(0).getName());
    String description= users.get(0).getDescription();
    Assert.assertEquals("A very weisuo person",description);


    Person person = personRepository.findMale();
    Assert.assertEquals("MaleA", person.getName());


      transactionTest.save();

    List<Person> userJacob = personRepository.findByName("Jacob");
     Assert.assertEquals(null, userJacob);
    List<Person> usersStone = personRepository.findByName("Stone");
    Assert.assertEquals(null, usersStone);

    Person userXF = new Person();
    userXF.setUuid(1002l);
    personRepository.del(userXF);
    System.out.println("DONE");
  }



}
