package stone.dal.jdbc.spring.adaptor.aop.example;

import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import stone.dal.jdbc.api.StJdbcTemplate;
import stone.dal.jdbc.spring.adaptor.aop.example.repo.PersonRepository;
import stone.dal.jdbc.spring.adaptor.app.SpringJdbcAdaptorTestApplication;
import stone.dal.models.Person;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringJdbcAdaptorTestApplication.class)
public class UserRepositoryTest {

  @Autowired
  private PersonRepository personRepository;

  @Autowired
  private StJdbcTemplate jdbcTemplate;

//  @Autowired
//  private PersonRepository userJpaRepository;

  //todo:call user repository

  @Before
  public void setup() {
    jdbcTemplate.execDcl("delete from person");
  }

  @Test
  public void testFindUserByManager() {
    Person user = new Person();
    user.setUuid(1002l);
    user.setName("Xie Feng");
    personRepository.create(user);

    List<Person> users = personRepository.findByName("Xie Feng");
    Assert.assertEquals("Xie Feng", users.get(0).getName());
  }
}
