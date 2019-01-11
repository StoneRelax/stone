package stone.dal.jdbc.spring.adaptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stone.dal.jdbc.JdbcDclRunner;
import stone.dal.jdbc.spring.adaptor.impl.JdbcDclRunnerImpl;

@Configuration
public class StoneSpringJdbcAdaptorAutoConfigure {

  @Bean
  public JdbcDclRunner getDclRunner() {
    return new JdbcDclRunnerImpl();
  }
}
