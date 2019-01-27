package stone.dal.adaptor.spring.jdbc.aop.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import stone.dal.adaptor.spring.jdbc.aop.example.repo.PersonRepository;

@Component
public class PersonService {
  @Autowired
  private PersonRepository personRepository;

  @Transactional
  public void testTx(Long uuid, String name, boolean testRollback) {
    if (testRollback) {
      personRepository.testTxRollback(uuid, name);
    } else {
      personRepository.textTxCommit(uuid, name);
    }
  }

}
