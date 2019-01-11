package stone.dal.jdbc.impl.aop;

import java.lang.reflect.Method;

/**
 * @author fengxie
 */
public class DalUpdatableMethodFilter extends DalMethodFilter {

	@Override
	public int accept(Method method) {
		String methodName = method.getName();
		if (methodName.startsWith("set") && !methodName.contains("_")) {
			return 0;
		}
		return super.accept(method);
	}
}
