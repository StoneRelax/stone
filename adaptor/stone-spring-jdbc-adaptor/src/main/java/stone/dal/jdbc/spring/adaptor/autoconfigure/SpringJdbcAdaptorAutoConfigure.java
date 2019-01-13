package stone.dal.jdbc.spring.adaptor.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import stone.dal.jdbc.JdbcTemplateSpi;
import stone.dal.jdbc.spring.adaptor.impl.JdbcTemplateSpiImpl;

@Configuration
public class SpringJdbcAdaptorAutoConfigure {

  @Bean
  public JdbcTemplateSpi getQueryRunner(@Autowired JdbcTemplate jdbcTemplate) {
    return new JdbcTemplateSpiImpl(jdbcTemplate);
  }
}
