package stone.dal.adaptor.spring.jdbc.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.ClassUtils;
import stone.dal.adaptor.spring.common.SpringContextHolder;
import stone.dal.adaptor.spring.common.aop.StRepoMethodPartRegistry;
import stone.dal.adaptor.spring.common.aop.StRepoQueryByMethodName;
import stone.dal.common.utils.DalClassUtils;
import stone.dal.jdbc.api.StJpaRepository;
import stone.dal.jdbc.impl.RdbmsEntity;
import stone.dal.jdbc.impl.RdbmsEntityManager;
import stone.dal.jdbc.impl.StJpaRepositoryImpl;
import stone.dal.kernel.utils.KernelRuntimeException;

import static stone.dal.kernel.utils.KernelUtils.setPropVal;

public class StJpaRepoMethodInterceptor implements MethodInterceptor {

  private static Logger logger = LoggerFactory.getLogger(StJpaRepoMethodInterceptor.class);

  @Override
  public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy)
      throws Throwable {
    StJpaRepositoryImpl jpaRepository = SpringContextHolder.getBean(StJpaRepositoryImpl.class);
    String methodName = method.getName();
    if (ClassUtils.hasMethod(StJpaRepository.class, methodName, method.getParameterTypes())) {
      Method m = ClassUtils.getMostSpecificMethod(method, StJpaRepository.class);
      if (m.getName().equalsIgnoreCase("findByPk")) {
        return findByPk(ClassUtils.getUserClass(o.getClass()), jpaRepository, objects);
      } else if (m.getName().equalsIgnoreCase("delByPk")) {
        deleteByPk(ClassUtils.getUserClass(o.getClass()), jpaRepository, objects);
        return null;
      } else if (m.getName().equalsIgnoreCase("findAll")) {
        return findAll(ClassUtils.getUserClass(o.getClass()), jpaRepository);
      } else {
        return m.invoke(jpaRepository, objects);
      }
    } else {
      StRepoQueryByMethodName query = StRepoMethodPartRegistry.getInstance().getQuery(method);
      if (query != null) {
        return query.query(method, objects);
      }
      return methodProxy.invoke(o, objects);
    }
  }


  private List findAll(Class repoClass, StJpaRepositoryImpl jpaRepository) {
    Class doClass = DalClassUtils.getDoClass(repoClass);
    if (doClass != null) {
      try {
        Object pkObj = doClass.newInstance();
        Method m = BeanUtils.findMethodWithMinimalParameters(StJpaRepositoryImpl.class, "findMany");
        return (List) m.invoke(jpaRepository, pkObj);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
        logger.error(e.getMessage());
        throw new KernelRuntimeException(e);
      }
    }
    throw new IllegalArgumentException(String.format("Can not find do class from the class (%s)", repoClass.getName()));
  }

  private void deleteByPk(Class repoClass, StJpaRepositoryImpl jpaRepository, Object[] objects) {
    Class doClass = DalClassUtils.getDoClass(repoClass);
    if (doClass != null) {
      RdbmsEntityManager entityMetaManager = SpringContextHolder.getBean(RdbmsEntityManager.class);
      RdbmsEntity entity = entityMetaManager.getEntity(doClass);
      try {
        Object pkObj = doClass.newInstance();
        entity.getPks().forEach(pkField -> {
          setPropVal(pkObj, pkField, objects[0]); //todo, here is a bug one pk isn't a primitive classd
        });
        Method m = BeanUtils.findMethodWithMinimalParameters(StJpaRepositoryImpl.class, "del");
        m.invoke(jpaRepository, pkObj);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
        logger.error(e.getMessage());
        throw new KernelRuntimeException(e);
      }
    } else {
      throw new IllegalArgumentException(
          String.format("Can not find do class from the class (%s)", repoClass.getName()));
    }
  }

  private Object findByPk(Class repoClass, StJpaRepositoryImpl jpaRepository, Object[] objects) {
    Class doClass = DalClassUtils.getDoClass(repoClass);
    if (doClass != null) {
      RdbmsEntityManager entityMetaManager = SpringContextHolder.getBean(RdbmsEntityManager.class);
      RdbmsEntity entity = entityMetaManager.getEntity(doClass);
      try {
        Object pkObj = doClass.newInstance();
        entity.getPks().forEach(pkField -> {
          setPropVal(pkObj, pkField, objects[0]); //todo, here is a bug one pk isn't a primitive classd
        });
//        BeanUtils.findMethod()
        Method m = BeanUtils.findMethodWithMinimalParameters(StJpaRepositoryImpl.class, "findOne");
        return m.invoke(jpaRepository, pkObj);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
        logger.error(e.getMessage());
        throw new KernelRuntimeException(e);
      }
    }
    throw new IllegalArgumentException(String.format("Can not find do class from the class (%s)", repoClass.getName()));
  }

}
