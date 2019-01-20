package stone.dal.adaptor.spring.jdbc.aop;

import java.lang.reflect.Method;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.ClassUtils;
import stone.dal.adaptor.spring.common.SpringContextHolder;
import stone.dal.adaptor.spring.common.aop.StRepoMethodPartRegistry;
import stone.dal.adaptor.spring.common.aop.StRepoQueryByMethodName;
import stone.dal.adaptor.spring.jdbc.api.StJpaRepository;
import stone.dal.adaptor.spring.jdbc.impl.StJpaRepositoryImpl;

public class StJpaRepoMethodInterceptor implements MethodInterceptor {

  @Override
  public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy)
      throws Throwable {
    StJpaRepositoryImpl jpaRepository = SpringContextHolder.getBean(StJpaRepositoryImpl.class);
    String methodName = method.getName();
    if (ClassUtils.hasMethod(StJpaRepository.class, methodName, method.getParameterTypes())) {
      Method m = ClassUtils.getMostSpecificMethod(method, StJpaRepository.class);
      return m.invoke(jpaRepository, objects);
    } else {
      StRepoQueryByMethodName query = StRepoMethodPartRegistry.getInstance().getQuery(method);
      if (query != null) {
        return query.query(method, objects);
      }
      return methodProxy.invoke(o, objects);
    }
  }

}
