package stone.dal.adaptor.spring.jdbc.spring.adaptor.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import stone.dal.adaptor.spring.common.annotation.EnableSequence;
import stone.dal.adaptor.spring.common.annotation.StRepositoryScan;

/**
 * @author fengxie
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "stone.dal.adaptor.spring.autoconfigure", "stone.dal.adaptor.spring.jdbc.impl",
    "stone.dal.starter.impl", "stone.dal.impl", "stone.dal.adaptor.spring.jdbc.spring.adaptor.init",
    "stone.dal.adaptor.spring.autoconfigure" })
@StRepositoryScan("stone.dal.adaptor.spring.jdbc.aop.example.repo")
@EntityScan("stone.dal.common.models")
@EnableSequence()
public class SpringJdbcAdaptorTestApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringJdbcAdaptorTestApplication.class, args);
  }
}
