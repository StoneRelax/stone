package stone.dal.adaptor.spring.jdbc.spring.adaptor.init;

import java.io.InputStream;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import stone.dal.adaptor.spring.jdbc.api.StJdbcTemplate;

/**
 * @author fengxie
 */
@Component
public class SpringJdbcAdaptorTestInitializer {

  @Autowired
  private StJdbcTemplate stJdbcTemplate;
//
//  @Autowired
//  private PersonRepository userRepository;
//
@PostConstruct
public void dbInit() {
  InputStream is = SpringJdbcAdaptorTestInitializer.class.getResourceAsStream("/dbscript.sql");
  stJdbcTemplate.execSqlStream(is);
}

}