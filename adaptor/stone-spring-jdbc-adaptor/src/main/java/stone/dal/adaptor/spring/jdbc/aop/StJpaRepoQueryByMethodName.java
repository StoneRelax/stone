package stone.dal.adaptor.spring.jdbc.aop;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import stone.dal.adaptor.spring.common.SpringContextHolder;
import stone.dal.adaptor.spring.common.aop.StRepoQueryByMethodName;
import stone.dal.adaptor.spring.jdbc.api.StJdbcTemplate;
import stone.dal.adaptor.spring.jdbc.api.meta.SqlCondition;
import stone.dal.adaptor.spring.jdbc.impl.RdbmsEntity;
import stone.dal.adaptor.spring.jdbc.impl.RdbmsEntityManager;
import stone.dal.common.models.meta.FieldMeta;
import stone.dal.kernel.utils.KernelUtils;

public class StJpaRepoQueryByMethodName extends StRepoQueryByMethodName {

    public StJpaRepoQueryByMethodName(Method method, PartTree tree) {
        super(method, tree);
    }

    @Override
    public Object query(Method method, Object[] params) {
        RdbmsEntityManager entityMetaManager = SpringContextHolder.getBean(RdbmsEntityManager.class);
        RdbmsEntity meta = entityMetaManager.getEntity(doClazz);
        StJdbcTemplate jdbcTemplate = SpringContextHolder.getBean(StJdbcTemplate.class);
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
