/*
 * File: $RCSfile: JBoltJDBCBaseMeta.java,v $
 *
 * Copyright (c) 2015 Dr0ne,
 *
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information
 * of Dr0ne ("Confidential Information"). You shall notCopyright (c) 2015 Dr0ne
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered
 * into with Dr0ne.
 */
package stone.dal.jdbc.api.meta;

import java.util.Map;

/**
 * The class <code>SqlBaseMeta</code> is responsible for storing sql statement and parameters
 *
 * @author feng.xie
 * @version $Revision: 1.4 $
 */
public abstract class SqlBaseMeta {
	/**
	 * Sql statement
	 */
	protected String sql;
	/**
	 * parameters
	 */
  protected Object[] parameters;
	/**
	 * Bean class whose instance might be imported with result value
	 */
	protected Class mappingClazz = Map.class;

	public String getSql() {
		return sql;
	}

  public Object[] getParameters() {
		return parameters;
	}

	public Class getMappingClazz() {
		return mappingClazz;
	}

	public static Factory factory() {
		return new Factory();
	}

	public static class Factory {
		private static Factory single = new Factory();

		private SqlBaseMeta meta = new SqlBaseMeta() {
		};

		public Factory sql(String sql) {
			meta.sql = sql;
			return this;
		}

		public Factory mappingClazz(Class clazz) {
			meta.mappingClazz = clazz;
			return this;
		}

    public Factory params(Object[] params) {
			meta.parameters = params;
			return this;
		}

		public SqlBaseMeta build() {
			return meta;
		}
	}
}