package stone.dal.adaptor.spring.jdbc.aop.example.repo;

import stone.dal.common.models.Person;

public abstract class PersonRepositoryImpl implements PersonRepository {

  @Override
  public Person findMale() {
    Person person = new Person();
    person.setName("MaleA");
    return person;
  }
}
