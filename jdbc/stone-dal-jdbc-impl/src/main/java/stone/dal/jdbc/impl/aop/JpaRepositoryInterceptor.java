package stone.dal.jdbc.impl.aop;

import java.lang.reflect.Method;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import stone.dal.jdbc.api.StJdbcTemplate;
import stone.dal.jdbc.api.StJpaRepository;
import stone.dal.jdbc.impl.RdbmsEntityManager;

public class JpaRepositoryInterceptor implements MethodInterceptor {

  private StJdbcTemplate stJdbcTemplate;

  private StJpaRepository stJpaRepository;

  private RdbmsEntityManager entityManager;

  @Override
  public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
    String methodName = method.getName();
    //parse method name
    //get params
    //create SqlQueryMeta
    //call stjdbc template
    return null;
  }
}
