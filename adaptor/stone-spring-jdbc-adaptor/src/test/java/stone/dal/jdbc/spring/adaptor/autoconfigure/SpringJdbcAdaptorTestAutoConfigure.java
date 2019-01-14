package stone.dal.jdbc.spring.adaptor.autoconfigure;

import org.springframework.context.annotation.Bean;
import stone.dal.jdbc.impl.aop.JpaRepositoryCglibFactory;
import stone.dal.jdbc.spring.adaptor.aop.example.UserRepository;

public class SpringJdbcAdaptorTestAutoConfigure {

  private JpaRepositoryCglibFactory cglibFactory;

  @Bean
  public UserRepository getUserRepository() {
    //todo:stone, factory creates an instance with aop method interceptor
    return cglibFactory.createProxy(UserRepository.class);
  }
}