package stone.dal.jdbc.spring.adaptor.init;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import stone.dal.jdbc.spring.adaptor.annotation.StRepositoryScan;

/**
 * @author fengxie
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
@StRepositoryScan("stone.dal.jdbc.spring.adaptor.aop.example")
public class SpringJdbcAdaptorTestApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringJdbcAdaptorTestApplication.class, args);
  }
}
