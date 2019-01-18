package stone.dal.adaptor.spring.jdbc.aop;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import stone.dal.adaptor.spring.autoconfigure.SpringContextHolder;
import stone.dal.adaptor.spring.jdbc.api.StJdbcTemplate;
import stone.dal.adaptor.spring.jdbc.api.meta.SqlCondition;
import stone.dal.adaptor.spring.jdbc.impl.RdbmsEntity;
import stone.dal.adaptor.spring.jdbc.impl.RdbmsEntityManager;
import stone.dal.adaptor.spring.utils.DalAopUtils;
import stone.dal.kernel.utils.KernelUtils;
import stone.dal.models.meta.FieldMeta;

public class StJpaRepoIntfMethodQuery {

    private PartTree tree;
    private Class doClazz;
    private RdbmsEntityManager entityMetaManager;
    private RdbmsEntity meta ;
    private Method method;

    public StJpaRepoIntfMethodQuery(Method method) {
        doClazz = DalAopUtils.getDoClass(method.getDeclaringClass());
        tree = StJpaRepoMethodPartRegistry.getInstance().getMethodPartTree(method);
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
