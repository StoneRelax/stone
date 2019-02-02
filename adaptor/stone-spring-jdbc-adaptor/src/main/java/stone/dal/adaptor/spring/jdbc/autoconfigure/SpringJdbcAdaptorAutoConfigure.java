package stone.dal.adaptor.spring.jdbc.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import stone.dal.adaptor.spring.jdbc.impl.JdbcTemplateSpiImpl;
import stone.dal.jdbc.spi.JdbcTemplateSpi;

@Configuration
public class SpringJdbcAdaptorAutoConfigure {

  @Bean
  public JdbcTemplateSpi getJdbcTemplateSpi(@Autowired JdbcTemplate jdbcTemplate) {
    return new JdbcTemplateSpiImpl(jdbcTemplate);
  }

}
