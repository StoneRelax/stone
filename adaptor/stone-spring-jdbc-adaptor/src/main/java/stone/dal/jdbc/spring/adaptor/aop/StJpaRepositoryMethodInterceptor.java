package stone.dal.jdbc.spring.adaptor.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.util.ClassUtils;
import stone.dal.jdbc.api.StJpaRepository;
import stone.dal.jdbc.api.meta.SqlCondition;
import stone.dal.jdbc.impl.RdbmsEntity;
import stone.dal.jdbc.impl.RdbmsEntityManager;
import stone.dal.jdbc.impl.StJdbcTemplateImpl;
import stone.dal.jdbc.impl.StJpaRepositoryImpl;
import stone.dal.jdbc.spring.adaptor.impl.SpringContextHolder;
import stone.dal.kernel.utils.KernelUtils;
import stone.dal.models.data.BaseDo;
import stone.dal.models.meta.FieldMeta;

public class StJpaRepositoryMethodInterceptor implements MethodInterceptor {

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
      CustomizedRepoIntfMethodQuery query = new CustomizedRepoIntfMethodQuery(method);
      return query.query(objects, jdbcTemplate);
    }
  }

}
