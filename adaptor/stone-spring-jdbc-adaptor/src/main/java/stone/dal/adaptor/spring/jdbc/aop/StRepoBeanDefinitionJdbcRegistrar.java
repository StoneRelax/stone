package stone.dal.adaptor.spring.jdbc.aop;

import stone.dal.adaptor.spring.common.aop.StRepoBeanDefinitionRegistrar;
import stone.dal.adaptor.spring.jdbc.annotation.StJpaRepositoryScan;

public class StRepoBeanDefinitionJdbcRegistrar extends StRepoBeanDefinitionRegistrar {
  @Override
  protected String getSupportPropsPath() {
    return "META-INF/dal-support.properties";
  }

  @Override
  protected Class getScanAnnotation() {
    return StJpaRepositoryScan.class;
  }
}
