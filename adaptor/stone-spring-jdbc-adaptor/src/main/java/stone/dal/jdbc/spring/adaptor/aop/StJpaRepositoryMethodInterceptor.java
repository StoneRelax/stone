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
    Object result = null;
    String methodName = method.getName();
    Class doClazz = getDoClass(o);
    if (ClassUtils.hasMethod(StJpaRepository.class, methodName, method.getParameterTypes())) {
      Method m = ClassUtils.getMostSpecificMethod(method, StJpaRepository.class);
      return m.invoke(jpaRepository, objects);
    } else {
      RdbmsEntity meta = entityMetaManager.getEntity(doClazz);
      SqlCondition condition = SqlCondition.create(doClazz);

      PartTree tree = new PartTree(methodName, doClazz);
      Iterator<PartTree.OrPart> orPartIterator = tree.iterator();
      int index = 0;
      while (orPartIterator.hasNext()) {
        PartTree.OrPart orPart = orPartIterator.next();
        Iterator<Part> andPartIterator = orPart.iterator();
        SqlCondition andCondition = SqlCondition.create(doClazz);
        while (andPartIterator.hasNext()) {
          Part andPart = andPartIterator.next();
          PropertyPath propertyPath = andPart.getProperty();
          FieldMeta fieldMeta = meta.getField(propertyPath.getSegment());
          andCondition.eq(fieldMeta.getDbName(), objects[index++]);
          if (andPartIterator.hasNext()) {
            andCondition.and();
          }
        }
        condition.join(andCondition);
        if (orPartIterator.hasNext()) {
          condition.or();
        }
      }

      List resultSet = jdbcTemplate.query(condition);
      if (!KernelUtils.isCollectionEmpty(resultSet)) {
        if (method.getReturnType().isAssignableFrom(List.class)) {
          result = resultSet;
        } else {
          result = resultSet.iterator().next();
        }
      }
      return result;
    }
  }

  private Class getDoClass(Object o) {
    Class doClazz = null;
    if (o instanceof StJpaRepository) {
      Class[] interfaces = o.getClass().getInterfaces();
      for (Class anInterface : interfaces) {
        Type[] genTypes = anInterface.getGenericInterfaces();
        if (genTypes != null && genTypes.length != 0) {
          Type genType = genTypes[0];
          Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
          for (Type param : params) {
            if (((Class) param).getSuperclass() == BaseDo.class) {
              doClazz = (Class) param;
              break;
            }
          }
        }

      }
    }
    return doClazz;
  }

}
