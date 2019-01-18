package stone.dal.jdbc.spring.adaptor.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import stone.dal.jdbc.api.StJdbcTemplate;
import stone.dal.jdbc.spring.adaptor.aop.example.repo.PersonRepository;

import javax.annotation.PostConstruct;

/**
 * @author fengxie
 */
@Component
public class SpringJdbcAdaptorTestInitializer {

  @Autowired
  private StJdbcTemplate stJdbcTemplate;

  @Autowired
  private PersonRepository userRepository;

//userRepository

}