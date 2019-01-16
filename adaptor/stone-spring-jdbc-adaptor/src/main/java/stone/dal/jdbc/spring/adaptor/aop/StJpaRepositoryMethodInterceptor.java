package stone.dal.jdbc.spring.adaptor.aop;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import stone.dal.jdbc.api.StJdbcTemplate;
import stone.dal.jdbc.api.StJpaRepository;
import stone.dal.jdbc.spring.adaptor.impl.SpringContextHolder;
import stone.dal.models.EntityMetaManager;
import stone.dal.models.data.BaseDo;
import stone.dal.models.meta.EntityMeta;

public class StJpaRepositoryMethodInterceptor implements MethodInterceptor {


    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) {
        EntityMetaManager entityMetaManager = SpringContextHolder.getBean(EntityMetaManager.class);
        StJdbcTemplate jdbcTemplate = SpringContextHolder.getBean(StJdbcTemplate.class);
        Object result = null;
        String methodName = method.getName();
        Class doClazz = getDoClass(o);
        System.out.println("Overriding method " + methodName);
        EntityMeta meta = entityMetaManager.getEntity(doClazz);

            //todo : get entity meta , generate SqlMeta , run with jdbcTemplate and return result
        return result;

    }

    private Class getDoClass(Object o) {
        Class doClazz = null;
        if (o instanceof StJpaRepository) {
            Class[] interfaces = o.getClass().getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                Class interfaceClazz = interfaces[i].getInterfaces().getClass();
                if (interfaceClazz != null && interfaceClazz == StJpaRepository.class) ;
                Type[] genTypes = interfaces[i].getGenericInterfaces();
                if (genTypes != null && genTypes.length != 0) {
                    Type genType = genTypes[0];
                    Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
                    for (int j = 0; j < params.length; j++) {
                        if (((Class) params[j]).getSuperclass() == BaseDo.class) {
                            doClazz = (Class) params[j];
                            break;
                        }
                    }
                }

            }
        }
        return doClazz;
    }

}