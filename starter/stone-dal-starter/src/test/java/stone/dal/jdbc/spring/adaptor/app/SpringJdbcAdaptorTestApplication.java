package stone.dal.jdbc.spring.adaptor.app;

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
@ComponentScan(basePackages = {"stone.dal.impl","stone.dal.jdbc.spring.adaptor.init"})
@StRepositoryScan("stone.dal.jdbc.spring.adaptor.aop.example.repo")
//@EnableJpaRepositories("stone.dal.jdbc.spring.adaptor.aop.example.repo")
//@EntityScan("stone.dal.models")
public class SpringJdbcAdaptorTestApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringJdbcAdaptorTestApplication.class, args);
  }
}
