package stone.dal.adaptor.spring.jdbc.aop.example;

import javax.annotation.PostConstruct;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import stone.dal.adaptor.spring.jdbc.aop.example.repo.PersonRepository;
import stone.dal.common.models.Person;

@RestController
@RequestMapping("/person")
public class PersonService {
  @Autowired
  private PersonRepository personRepository;

  @PostConstruct
  public void init() {
    Person user = new Person();
    user.setUuid(1l);
    user.setName("Xie Feng");
    personRepository.create(user);
  }

  @Transactional
  @Operation(method = "GET", description = "Query ${doName}")
  public void testTx(Long uuid, String name, boolean testRollback) {
    if (testRollback) {
      personRepository.testTxRollback(uuid, name);
    } else {
      personRepository.textTxCommit(uuid, name);
    }
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @Operation(method = "GET", description = "Query ${doName}")
  public @ResponseBody
  Person get(@PathVariable Long id) {
    Person person = new Person();
    person.setUuid(id);
    return personRepository.findOne(person);
  }

  @Transactional
  @RequestMapping(value = "/", method = RequestMethod.POST)
  @Operation(method = "POST", description = "Create ${doName}")
  public @ResponseBody
  Long create(@RequestBody Person person) {
    return personRepository.create(person);
  }

  @Transactional
  @RequestMapping(value = "/", method = RequestMethod.PUT)
  @Operation(method = "PUT", description = "Update ${doName}")
  public @ResponseBody
  void update(@RequestBody Person person) {
    personRepository.update(person);
  }

  @Transactional
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @Operation(method = "DELETE", description = "Delete ${doName}")
  public @ResponseBody
  void delete(@PathVariable long id) {
    Person person = new Person();
    person.setUuid(id);
    personRepository.del(person);
  }

}
