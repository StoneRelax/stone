package stone.dal.adaptor.spring.jdbc.aop.example;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.annotation.PostConstruct;

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
@Api(value = "/person", description = "${className}")
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
  @ApiOperation(httpMethod = "GET", value = "Query ${doName}", notes = "Find ${doName} by uuid")
  public void testTx(Long uuid, String name, boolean testRollback) {
    if (testRollback) {
      personRepository.testTxRollback(uuid, name);
    } else {
      personRepository.textTxCommit(uuid, name);
    }
  }

  @ApiParam
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @ApiOperation(httpMethod = "GET", value = "Query ${doName}", notes = "Find ${doName} by uuid")
  @ApiImplicitParams(
      @ApiImplicitParam(name = "id", value = "${doName} id", required = true, paramType = "path")
  )
  public @ResponseBody
  Person get(@PathVariable Long id) {
    Person person = new Person();
    person.setUuid(id);
    return personRepository.findOne(person);
  }

  @Transactional
  @RequestMapping(value = "/", method = RequestMethod.POST)
  @ApiOperation(httpMethod = "POST", value = "Create ${doName}", notes = "Create ${doName}")
  public @ResponseBody
  Long create(@RequestBody Person person) {
    return personRepository.create(person);
  }

  @Transactional
  @RequestMapping(value = "/", method = RequestMethod.PUT)
  @ApiOperation(httpMethod = "PUT", value = "Update ${doName}", notes = "Update ${doName}")
  public @ResponseBody
  void update(@RequestBody Person person) {
    personRepository.update(person);
  }

  @Transactional
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @ApiOperation(httpMethod = "DELETE", value = "Delete ${doName}", notes = "Delete ${doName} by uuid")
  @ApiImplicitParams(
      @ApiImplicitParam(name = "id", value = "${doName} id", required = true, paramType = "path")
  )
  public @ResponseBody
  void delete(@PathVariable long id) {
    Person person = new Person();
    person.setUuid(id);
    personRepository.del(person);
  }

}
