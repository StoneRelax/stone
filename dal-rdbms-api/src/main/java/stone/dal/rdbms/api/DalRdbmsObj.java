package stone.dal.rdbms.api;

import stone.dal.common.api.DalObj;
import stone.dal.rdbms.api.meta.SqlCondition;

/**
 * @author fengxie
 */
public class DalRdbmsObj extends DalObj {

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
