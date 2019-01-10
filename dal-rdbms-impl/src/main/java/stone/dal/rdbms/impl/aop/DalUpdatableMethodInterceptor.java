package stone.dal.rdbms.impl.aop;

import org.springframework.cglib.proxy.MethodProxy;
import stone.dal.common.api.DalObj;
import stone.dal.common.api.DalStates;
import stone.dal.kernel.StringUtils;
import stone.dal.rdbms.impl.utils.DalLazyLoadQueryMetaBuilder;

import java.lang.reflect.Method;

/**
 * @author fengxie
 */
public class DalUpdatableMethodInterceptor extends DalMethodInterceptor {

	public DalUpdatableMethodInterceptor(DalLazyLoadQueryMetaBuilder dalLazyLoadQueryMetaBuilder) {
		super(dalLazyLoadQueryMetaBuilder);
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		String methodName = method.getName();
		if (methodName.startsWith("set")) {
			methodProxy.invokeSuper(obj, objects);
			if (((DalObj) obj).monitor()) {
				String fieldName = StringUtils.firstChar2UpperCase(
						methodName.replace("set", ""));
				((DalObj) obj).get_changes().add(fieldName);
				((DalObj) obj).set_state(DalStates.UPDATED);
			}
			return null;
		}
		return super.intercept(obj, method, objects, methodProxy);
	}
}
