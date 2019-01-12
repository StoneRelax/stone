package stone.dal.jdbc.impl;

import java.util.HashMap;
import java.util.Map;
import stone.dal.jdbc.JdbcResultHandlerSpi;
import stone.dal.jdbc.api.ex.CreateRowObjectException;
import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.jdbc.impl.aop.DalMethodFilter;
import stone.dal.jdbc.impl.aop.DalMethodInterceptor;
import stone.dal.jdbc.impl.aop.DalUpdatableMethodFilter;
import stone.dal.jdbc.impl.aop.DalUpdatableMethodInterceptor;
import stone.dal.jdbc.impl.utils.LazyLoadQueryMetaBuilder;
import stone.dal.kernel.utils.CGLibUtils;

/**
 * @author fengxie
 */
public class JdbcResultHandlerSpiImpl implements JdbcResultHandlerSpi {

	private DalUpdatableMethodFilter updatableMethodFilter;
	private DalMethodFilter methodFilter;
	private DalMethodInterceptor methodInterceptor;
	private DalUpdatableMethodInterceptor updatableMethodInterceptor;

	public JdbcResultHandlerSpiImpl(LazyLoadQueryMetaBuilder loader) {
		this.updatableMethodFilter = new DalUpdatableMethodFilter();
		this.methodFilter = new DalMethodFilter();
		this.methodInterceptor = new DalMethodInterceptor(loader);
		this.updatableMethodInterceptor = new DalUpdatableMethodInterceptor(loader);
	}

	@Override
	public Object buildRowObj(SqlQueryMeta queryMeta) throws CreateRowObjectException {
		Object rowObj;
		Class clazz = queryMeta.getMappingClazz();
		if (clazz != null && clazz != Map.class) {
			try {
				if (queryMeta.isUpdatable()) {
					rowObj = CGLibUtils.buildProxyClass(clazz, updatableMethodInterceptor, updatableMethodFilter);
				} else if (queryMeta.isSupportFetchMore()) {
					rowObj = CGLibUtils.buildProxyClass(clazz, methodInterceptor, methodFilter);
				} else {
					rowObj = clazz.newInstance();
				}
			} catch (Exception e) {
				throw new CreateRowObjectException(e);
			}
		} else {
			rowObj = new HashMap();
		}
		return rowObj;
	}
}
