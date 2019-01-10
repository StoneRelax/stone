package stone.dal.rdbms.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stone.dal.kernel.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author fengxie
 */
public class DalRdbmsDclRunner extends DalKernel {

	private static Logger logger = LoggerFactory.getLogger(DalRdbmsDclRunner.class);

	public int run(String schema, String sql) {
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = getConnection(schema);
			ps = createUpdatableStatement(conn, sql);
			int updateRows = ps.executeUpdate();
			logger.info("Update rows {}", updateRows);
			return updateRows;
		} catch (Exception e) {
			Exception ex = wrapException(schema, e);
			LogUtils.error(logger, ex);
		} finally {
			release(schema, ps);
			release(schema, conn);
		}
		return -1;
	}

	public static Factory factory() {
		return new Factory();
	}

	public static class Factory extends DalKernel.Factory<DalRdbmsDclRunner> {
		@Override
		protected DalRdbmsDclRunner initRunner() {
			return new DalRdbmsDclRunner();
		}
	}
}
