package stone.dal.tools.rdbms.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import stone.dal.adaptor.spring.common.annotation.EnableSequence;

/**
 * @author fengxie
 */
@Configuration
@EnableAutoConfiguration
@EntityScan("stone.dal.tools.rdbms.models")
@EnableSequence()
public class RdbmsSyncTestApplication {
  public static void main(String[] args) {
    SpringApplication.run(RdbmsSyncTestApplication.class, args);
  }
}
