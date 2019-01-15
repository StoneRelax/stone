package stone.dal.jdbc.spring.adaptor.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stone.dal.impl.DalMethodFilter;
import stone.dal.impl.DalRepositoryHandlerImpl;
import stone.dal.impl.DalRepositoryMethodInterceptor;
import stone.dal.jdbc.impl.aop.JpaRepositoryCglibFactory;
import stone.dal.jdbc.spring.adaptor.aop.example.UserRepository;

@Configuration
public class SpringJdbcAdaptorTestAutoConfigure {

  private JpaRepositoryCglibFactory cglibFactory;

  @Autowired
  private DalRepositoryHandlerImpl dalRepositoryHandler;

  @Autowired
  DalRepositoryMethodInterceptor dalRepositoryMethodInterceptor;

  @Autowired
  DalMethodFilter dalMethodFilter;

  @Bean
  public DalRepositoryMethodInterceptor getDalRepositoryMethodInterceptor(){
    return new DalRepositoryMethodInterceptor();
  }

  @Bean
  public DalMethodFilter getDalMethodFilter(){
    return new DalMethodFilter();
  }


  @Bean
  public DalRepositoryHandlerImpl getDalRepositoryHandlerImpl(@Autowired DalRepositoryMethodInterceptor dalRepositoryMethodInterceptor,@Autowired DalMethodFilter dalMethodFilter ){
    return new DalRepositoryHandlerImpl(dalRepositoryMethodInterceptor,dalMethodFilter);
  }

  @Bean
  public UserRepository getUserRepository(@Autowired DalRepositoryHandlerImpl dalRepositoryHandler){
    return (UserRepository)dalRepositoryHandler.build(UserRepository.class);
  }

//  @Bean
//  public UserRepository getUserRepository() {
//    //todo:stone, factory creates an instance with aop method interceptor
//    return cglibFactory.createProxy(UserRepository.class);
//  }
}
