package stone.dal.adaptor.spring.jdbc.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.ClassUtils;
import stone.dal.adaptor.spring.autoconfigure.SpringContextHolder;
import stone.dal.adaptor.spring.jdbc.api.StJpaRepository;
import stone.dal.adaptor.spring.jdbc.impl.RdbmsEntityManager;
import stone.dal.adaptor.spring.jdbc.impl.StJdbcTemplateImpl;
import stone.dal.adaptor.spring.jdbc.impl.StJpaRepositoryImpl;

public class StJpaRepoMethodInterceptor implements MethodInterceptor {

  @Override
  public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy)
      throws InvocationTargetException, IllegalAccessException {
    RdbmsEntityManager entityMetaManager = SpringContextHolder.getBean(RdbmsEntityManager.class);
    StJdbcTemplateImpl jdbcTemplate = SpringContextHolder.getBean(StJdbcTemplateImpl.class);
    StJpaRepositoryImpl jpaRepository = SpringContextHolder.getBean(StJpaRepositoryImpl.class);
    String methodName = method.getName();
    if (ClassUtils.hasMethod(StJpaRepository.class, methodName, method.getParameterTypes())) {
      Method m = ClassUtils.getMostSpecificMethod(method, StJpaRepository.class);
      return m.invoke(jpaRepository, objects);
    } else {
      StJpaRepoIntfMethodQuery query = new StJpaRepoIntfMethodQuery(method);
      return query.query(objects, jdbcTemplate);
    }
  }

}
