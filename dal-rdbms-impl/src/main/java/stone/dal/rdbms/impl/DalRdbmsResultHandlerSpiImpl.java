package stone.dal.rdbms.impl;


import stone.dal.kernel.CGLibUtils;
import stone.dal.kernel.KernelRuntimeException;
import stone.dal.metadata.meta.PlatformRuntimeException;
import stone.dal.rdbms.api.meta.SqlQueryMeta;
import stone.dal.rdbms.impl.aop.DalMethodFilter;
import stone.dal.rdbms.impl.aop.DalMethodInterceptor;
import stone.dal.rdbms.impl.aop.DalUpdatableMethodFilter;
import stone.dal.rdbms.impl.aop.DalUpdatableMethodInterceptor;
import stone.dal.rdbms.impl.utils.DalLazyLoadQueryMetaBuilder;
import stone.dal.rdbms.spi.DalRdbmsResultHandlerSpi;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fengxie
 */
public class DalRdbmsResultHandlerSpiImpl implements DalRdbmsResultHandlerSpi {

	private DalUpdatableMethodFilter updatableMethodFilter;
	private DalMethodFilter methodFilter;
	private DalMethodInterceptor methodInterceptor;
	private DalUpdatableMethodInterceptor updatableMethodInterceptor;

	public DalRdbmsResultHandlerSpiImpl(DalLazyLoadQueryMetaBuilder loader) {
		this.updatableMethodFilter = new DalUpdatableMethodFilter();
		this.methodFilter = new DalMethodFilter();
		this.methodInterceptor = new DalMethodInterceptor(loader);
		this.updatableMethodInterceptor = new DalUpdatableMethodInterceptor(loader);
	}

	@Override
	public Object buildRowObj(SqlQueryMeta queryMeta) throws KernelRuntimeException {
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
				throw new PlatformRuntimeException(e);
			}
		} else {
			rowObj = new HashMap();
		}
		return rowObj;
	}
}
