package stone.dal.jdbc.impl.aop;

import java.lang.reflect.Method;
import org.springframework.cglib.proxy.MethodProxy;
import stone.dal.jdbc.impl.utils.LazyLoadQueryMetaBuilder;
import stone.dal.kernel.utils.StringUtils;
import stone.dal.models.data.BaseDo;

/**
 * @author fengxie
 */
public class DalUpdatableMethodInterceptor extends DalMethodInterceptor {

	public DalUpdatableMethodInterceptor(LazyLoadQueryMetaBuilder lazyLoadQueryMetaBuilder) {
		super(lazyLoadQueryMetaBuilder);
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		String methodName = method.getName();
		if (methodName.startsWith("set")) {
			methodProxy.invokeSuper(obj, objects);
			if (((BaseDo) obj).monitor()) {
				String fieldName = StringUtils.firstChar2UpperCase(
						methodName.replace("set", ""));
				((BaseDo) obj).ackChange(fieldName);
				((BaseDo) obj).set_state(BaseDo.States.UPDATED);
			}
			return null;
		}
		return super.intercept(obj, method, objects, methodProxy);
	}
}
