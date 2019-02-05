package stone.dal.adaptor.es.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import stone.dal.adaptor.spring.common.annotation.EnableSequence;
import stone.dal.adaptor.spring.jdbc.annotation.StJpaRepositoryScan;

/**
 * @author fengxie
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableTransactionManagement
@ComponentScan(basePackages = {"stone.dal.spring.es.autoconfigure","stone.dal.spring.es.impl","stone.dal.spring.es.lib", "stone.dal.adaptor.spring.jdbc.autoconfigure", "stone.dal.adaptor.spring.jdbc.stone.dal.spring.es.impl",
    "stone.dal.starter.stone.dal.spring.es.impl", "stone.dal.stone.dal.spring.es.impl", "stone.dal.jdbc.spring.adaptor.init",
    "stone.dal.adaptor.spring.jdbc.autoconfigure",
        "stone.dal.adaptor.es.example","stone.dal.adaptor.es.autoconfigure"})
@StJpaRepositoryScan("stone.dal.adaptor.es.example.repo")
@EntityScan("stone.dal.common.models")
@EnableSequence()
public class SpringEsAdaptorTestApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringEsAdaptorTestApplication.class, args);
  }
}
