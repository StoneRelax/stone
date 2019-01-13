package stone.dal.jdbc.impl.aop;

import java.lang.reflect.Method;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import stone.dal.jdbc.impl.utils.RelationQueryBuilder;

/**
 * @author fengxie
 */
public class DalMethodInterceptor implements MethodInterceptor {

  private RelationQueryBuilder relationQueryBuilder;

  public DalMethodInterceptor(RelationQueryBuilder relationQueryBuilder) {
    this.relationQueryBuilder = relationQueryBuilder;
  }

  public Object intercept(
      Object obj, Method method, Object[] objects,
      MethodProxy methodProxy) throws Throwable {
//		Object result = methodProxy.invokeSuper(obj, objects);
//		if (obj instanceof BaseDo) {
//			String propertyName =
//					StringUtils.firstChar2LowerCase(
//							org.apache.commons.lang.StringUtils.replace(method.getName(), "get", "")
//					);
//			if (!((BaseDo) obj).isLoaded(propertyName)) {
//				SqlQueryMeta queryMeta = relationQueryBuilder.buildMetaFactory(obj, propertyName).supportFetchMore(true).build();
//				List resultSet = JdbcQuerySpi.factory().getRunner().exec(queryMeta);
//				if (!isCollectionEmpty(resultSet)) {
//					if (method.getReturnType().isAssignableFrom(List.class)) {
//						result = resultSet;
//					} else {
//						result = resultSet.iterator().next();
//					}
//				}
//				methodProxy.invokeSuper(obj, objects);
//				((BaseDo) obj).markLazyLoadedField(propertyName);
//			}
//		}
//		return result;
    return null;
  }

}