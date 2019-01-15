package stone.dal.jdbc.spring.adaptor.init;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import stone.dal.impl.SpringContextHolder;
import stone.dal.jdbc.api.StJdbcTemplate;
import stone.dal.jdbc.spring.adaptor.aop.example.UserRepository;

import java.io.InputStream;

/**
 * @author fengxie
 */
@Component
public class SpringJdbcAdaptorTestInitializer {

  @Autowired
  private StJdbcTemplate stJdbcTemplate;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private SpringContextHolder holder;

  @PostConstruct
  public void dbInit() {
    InputStream is = SpringJdbcAdaptorTestInitializer.class.getResourceAsStream("/dbscript.sql");
    stJdbcTemplate.execSqlStream(is);
    userRepository.findUserByManager(true);
  }

}