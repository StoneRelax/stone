package stone.dal.jdbc.spring.adaptor.aop;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import stone.dal.jdbc.api.StJdbcTemplate;
import stone.dal.jdbc.api.StJpaRepository;
import stone.dal.jdbc.api.meta.SqlCondition;
import stone.dal.jdbc.impl.RdbmsEntity;
import stone.dal.jdbc.impl.RdbmsEntityManager;
import stone.dal.kernel.utils.KernelUtils;
import stone.dal.models.data.BaseDo;
import stone.dal.models.meta.FieldMeta;
import stone.dal.starter.impl.SpringContextHolder;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

public class StJpaRepositoryMethodInterceptor implements MethodInterceptor {


    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) {
        RdbmsEntityManager entityMetaManager = SpringContextHolder.getBean(RdbmsEntityManager.class);
        StJdbcTemplate jdbcTemplate = SpringContextHolder.getBean(StJdbcTemplate.class);
        Object result = null;
        String methodName = method.getName();
        Class doClazz = getDoClass(o);
        System.out.println("Overriding method " + methodName);
        RdbmsEntity meta = entityMetaManager.getEntity(doClazz);
        SqlCondition condition = SqlCondition.create(doClazz);

        PartTree tree = new PartTree(methodName,doClazz);
        Iterator<PartTree.OrPart> orPartIterator = tree.iterator();
        while (orPartIterator.hasNext()) {
            PartTree.OrPart orPart = orPartIterator.next();
            Iterator<Part> andPartIterator = orPart.iterator();
            SqlCondition andCondition = SqlCondition.create(doClazz);
            while (andPartIterator.hasNext()) {
                Part andPart = andPartIterator.next();
                PropertyPath propertyPath = andPart.getProperty();
                FieldMeta fieldMeta = meta.getField(propertyPath.getSegment());
                andCondition.eq(fieldMeta.getDbName(),objects);
                if(andPartIterator.hasNext()){
                    andCondition.and();
                }
            }
            condition.join(andCondition);
            if(orPartIterator.hasNext()){
                condition.or();
            }
        }

        List resultSet = jdbcTemplate.query(condition.build());
        if (!KernelUtils.isCollectionEmpty(resultSet)) {
            if (method.getReturnType().isAssignableFrom(List.class)) {
                result = resultSet;
            } else {
                result = resultSet.iterator().next();
            }
        }
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
