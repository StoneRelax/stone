package stone.dal.jdbc.spring.adaptor.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import stone.dal.adaptor.spring.seq.EnableSequence;
import stone.dal.jdbc.spring.adaptor.annotation.StRepositoryScan;

/**
 * @author fengxie
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"stone.dal.starter.impl", "stone.dal.impl", "stone.dal.jdbc.spring.adaptor.init","stone.dal.jdbc.spring.adaptor.autoconfigure"})
@StRepositoryScan("stone.dal.jdbc.spring.adaptor.aop.example.repo")
@EntityScan("stone.dal.models")
@EnableSequence()
public class SpringJdbcAdaptorTestApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringJdbcAdaptorTestApplication.class, args);
  }
}
