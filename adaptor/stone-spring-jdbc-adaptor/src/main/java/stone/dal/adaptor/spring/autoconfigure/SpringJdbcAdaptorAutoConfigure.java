package stone.dal.adaptor.spring.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import stone.dal.adaptor.spring.jdbc.impl.JdbcTemplateSpiImpl;
import stone.dal.adaptor.spring.jdbc.impl.SequenceSpiImpl;
import stone.dal.adaptor.spring.jdbc.spi.JdbcTemplateSpi;
import stone.dal.adaptor.spring.jdbc.spi.SequenceSpi;
import stone.dal.seq.api.SequenceManager;

@Configuration
public class SpringJdbcAdaptorAutoConfigure {

  @Autowired
  private SequenceManager sequenceManager;

  @Bean
  public JdbcTemplateSpi getJdbcTemplateSpi(@Autowired JdbcTemplate jdbcTemplate) {
    return new JdbcTemplateSpiImpl(jdbcTemplate);
  }


  @Bean
  public SequenceSpi getSequence() {
    return new SequenceSpiImpl(sequenceManager);
  }

  @Bean
  public SpringContextHolder getSpringContextHolder() {
    return new SpringContextHolder();
  }

}
