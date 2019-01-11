package stone.dal.jdbc.api;

import stone.dal.jdbc.api.meta.SqlCondition;
import stone.dal.models.data.BaseDo;

/**
 * @author fengxie
 */
public class DalRdbmsObj extends BaseDo {

	@SqlOperator(name = "=")
	public SqlCondition buildEqCondition() {
		throw new RuntimeException("Please call DalObjFactory to build equals condition");
	}

	@SqlOperator(name = "<")
	public SqlCondition buildLessCondition() {
		throw new RuntimeException("Please call DalObjFactory to build less condition");
	}

	@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD})
	@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
	public @interface SqlOperator {
		String name();
	}
}
