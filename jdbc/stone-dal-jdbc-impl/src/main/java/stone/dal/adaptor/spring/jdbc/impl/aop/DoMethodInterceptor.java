package stone.dal.adaptor.spring.jdbc.impl.aop;

import java.lang.reflect.Method;
import java.util.List;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import stone.dal.adaptor.spring.jdbc.api.meta.SqlQueryMeta;
import stone.dal.adaptor.spring.jdbc.impl.DefaultRowMapper;
import stone.dal.adaptor.spring.jdbc.impl.RdbmsEntityManager;
import stone.dal.adaptor.spring.jdbc.impl.utils.RelationQueryBuilder;
import stone.dal.adaptor.spring.jdbc.spi.DBDialectSpi;
import stone.dal.adaptor.spring.jdbc.spi.JdbcTemplateSpi;
import stone.dal.kernel.utils.StringUtils;
import stone.dal.models.data.BaseDo;

import static stone.dal.kernel.utils.KernelUtils.isCollectionEmpty;

/**
 * @author fengxie
 */
public class DoMethodInterceptor implements MethodInterceptor {

  private RelationQueryBuilder relationQueryBuilder;

  private RdbmsEntityManager entityMetaManager;

  private JdbcTemplateSpi jdbcTemplateSpi;

  private DBDialectSpi dbDialectSpi;

  private boolean supportMarkDirty;

  public DoMethodInterceptor(
      RelationQueryBuilder relationQueryBuilder,
      JdbcTemplateSpi jdbcTemplateSpi,
      DBDialectSpi dbDialectSpi,
      RdbmsEntityManager entityMetaManager,
      boolean supportMarkDirty) {
    this.relationQueryBuilder = relationQueryBuilder;
    this.jdbcTemplateSpi = jdbcTemplateSpi;
    this.supportMarkDirty = supportMarkDirty;
    this.dbDialectSpi = dbDialectSpi;
    this.entityMetaManager = entityMetaManager;
  }

  public Object intercept(
      Object obj, Method method, Object[] objects,
      MethodProxy methodProxy) throws Throwable {
    if (supportMarkDirty) {
      boolean isContinue = markDirty(obj, method, objects, methodProxy);
      if (!isContinue) {
        return null;
      }
    }
    return lazyLoad(obj, method, objects, methodProxy);
  }

  private boolean markDirty(Object obj, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
    if (obj instanceof BaseDo) {
      String methodName = method.getName();
      if (methodName.startsWith("set")) {
        methodProxy.invokeSuper(obj, objects);
        if (((BaseDo) obj).monitor()) {
          String fieldName = StringUtils.firstChar2UpperCase(
              methodName.replace("set", ""));
          ((BaseDo) obj).ackChange(fieldName);
          ((BaseDo) obj).set_state(BaseDo.States.UPDATED);
        }
        return false;
      }
    }
    return true;
  }

  private Object lazyLoad(Object obj, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
    Object result = methodProxy.invokeSuper(obj, objects);
    if (obj instanceof BaseDo) {
      String propertyName =
          StringUtils.firstChar2LowerCase(
              org.apache.commons.lang.StringUtils.replace(method.getName(), "get", "")
          );
      if (!((BaseDo) obj).isLoaded(propertyName)) {
        SqlQueryMeta queryMeta = relationQueryBuilder.buildMetaFactory(obj, propertyName).supportFetchMore(true)
            .build();
        List resultSet = jdbcTemplateSpi.query(queryMeta,
            new DefaultRowMapper(dbDialectSpi, entityMetaManager, relationQueryBuilder, jdbcTemplateSpi));
        if (!isCollectionEmpty(resultSet)) {
          if (method.getReturnType().isAssignableFrom(List.class)) {
            result = resultSet;
          } else {
            result = resultSet.iterator().next();
          }
        }
        methodProxy.invokeSuper(obj, objects);
        ((BaseDo) obj).markLazyLoadedField(propertyName);
      }
    }
    return result;
  }

  public static class LazyLoad extends DoMethodInterceptor {
    public LazyLoad(RelationQueryBuilder relationQueryBuilder,
        JdbcTemplateSpi jdbcTemplateSpi,
        DBDialectSpi dbDialectSpi,
        RdbmsEntityManager entityMetaManager) {
      super(relationQueryBuilder, jdbcTemplateSpi, dbDialectSpi, entityMetaManager, false);
    }
  }

  public static class DirtyMark extends DoMethodInterceptor {
    public DirtyMark(RelationQueryBuilder relationQueryBuilder,
        JdbcTemplateSpi jdbcTemplateSpi,
        DBDialectSpi dbDialectSpi,
        RdbmsEntityManager entityMetaManager) {
      super(relationQueryBuilder, jdbcTemplateSpi, dbDialectSpi, entityMetaManager, true);
    }
  }

}