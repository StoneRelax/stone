package stone.dal.jdbc.impl.aop;

import java.lang.reflect.Method;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import stone.dal.jdbc.impl.utils.DalLazyLoadQueryMetaBuilder;

/**
 * @author fengxie
 */
public class DalMethodInterceptor implements MethodInterceptor {

  private DalLazyLoadQueryMetaBuilder dalLazyLoadQueryMetaBuilder;

  public DalMethodInterceptor(DalLazyLoadQueryMetaBuilder dalLazyLoadQueryMetaBuilder) {
    this.dalLazyLoadQueryMetaBuilder = dalLazyLoadQueryMetaBuilder;
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
//			if (!((DalObj) obj).isLoaded(propertyName)) {
//				SqlQueryMeta queryMeta = dalLazyLoadQueryMetaBuilder.buildMetaFactory(obj, propertyName).supportFetchMore(true).build();
//				List resultSet = DalRdbmsQueryRunner.factory().getRunner().run(queryMeta);
//				if (!list_emp(resultSet)) {
//					if (method.getReturnType().isAssignableFrom(List.class)) {
//						result = resultSet;
//					} else {
//						result = resultSet.iterator().next();
//					}
//				}
//				methodProxy.invokeSuper(obj, objects);
//				((DalObj) obj).markLazyLoadedField(propertyName);
//			}
//		}
//		return result;
    return null;
  }

}