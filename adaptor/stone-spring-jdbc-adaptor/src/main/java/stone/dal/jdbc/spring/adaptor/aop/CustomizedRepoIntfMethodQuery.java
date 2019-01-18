package stone.dal.jdbc.spring.adaptor.aop;

import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import stone.dal.jdbc.api.StJdbcTemplate;
import stone.dal.jdbc.api.meta.SqlCondition;
import stone.dal.jdbc.impl.RdbmsEntity;
import stone.dal.jdbc.impl.RdbmsEntityManager;
import stone.dal.jdbc.spring.adaptor.impl.SpringContextHolder;
import stone.dal.kernel.utils.ClassUtils;
import stone.dal.kernel.utils.KernelUtils;
import stone.dal.models.meta.FieldMeta;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

public class CustomizedRepoIntfMethodQuery {

    private PartTree tree;
    private Class doClazz;
    private RdbmsEntityManager entityMetaManager;
    private RdbmsEntity meta ;
    private Method method;

    public CustomizedRepoIntfMethodQuery(Method method) {
        doClazz = ClassUtils.getDoClass(method.getDeclaringClass());
        tree = StJpaRepositoryMethodPartCache.getInstance().getMethodPartTree(method);
        entityMetaManager = SpringContextHolder.getBean(RdbmsEntityManager.class);
        meta = entityMetaManager.getEntity(doClazz);
        this.method = method;
    }

    public Object query(Object[] params, StJdbcTemplate jdbcTemplate) {
        Object result = null;
        SqlCondition condition = SqlCondition.create(doClazz);
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
                andCondition.eq(fieldMeta.getDbName(), params[index++]);
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
