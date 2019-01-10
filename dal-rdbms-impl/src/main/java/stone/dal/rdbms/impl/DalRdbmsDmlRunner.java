package stone.dal.rdbms.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stone.dal.common.api.DalObj;
import stone.dal.common.api.meta.EntityMeta;
import stone.dal.rdbms.api.meta.SqlDmlDclMeta;
import stone.dal.spi.DalSequenceSpi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Set;

import static stone.dal.kernel.KernelUtils.arr_2_str;
import static stone.dal.kernel.KernelUtils.get_v;
import static stone.dal.kernel.KernelUtils.set_v;

/**
 * @author fengxie
 */
public class DalRdbmsDmlRunner extends DalKernel {

	private static Logger logger = LoggerFactory.getLogger(DalRdbmsDmlRunner.class);
	private DalSequenceSpi dalSequence;

	public int run(SqlDmlDclMeta meta) {
		logger.info("SQL:{}", meta.getSql().toUpperCase());
		logger.info("PARAMETERS:[{}]", arr_2_str(meta.getParameters(), ","));
		PreparedStatement ps = null;
		Connection conn = null;
		String schema = meta.getSchema();
		try {
			conn = getConnection(schema);
			ps = createUpdatableStatement(conn, meta.getSql());
			Object[] params = meta.getParameters();
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					Object param = params[i];
					setStatementParams(ps, param, i);
				}
			}
			int updateRows = ps.executeUpdate();
			logger.info("Update rows {}", updateRows);
			return updateRows;
		} catch (Exception e) {
			throw wrapException(schema, e);
		} finally {
			release(schema, ps);
			release(schema, conn);
		}
	}

	public void runInsert(DalObj obj) {
		runInsert(obj, null);
	}

	public void runDelete(DalObj obj) {
		runDelete(obj, null);
	}

	public void runDelete(DalObj obj, String schema) {
		EntityMeta meta = dalEntityMetaManager.getEntity(obj.getClass());
		DalRdbmsEntity entity = DalRdbmsEntityManager.getInstance().build(meta);
		SqlDmlDclMeta sqlDmlDclMeta = entity.getDeleteMeta(obj, schema);
		run(sqlDmlDclMeta);
	}

	public void runInsert(DalObj obj, String schema) {
		EntityMeta meta = dalEntityMetaManager.getEntity(obj.getClass());
		DalRdbmsEntity entity = DalRdbmsEntityManager.getInstance().build(meta);
		bindSequenceValues(obj, entity);
		SqlDmlDclMeta sqlDmlDclMeta = entity.getInsertMeta(obj, schema);
		run(sqlDmlDclMeta);
	}

	public void runUpdate(DalObj obj, String schema) {
		EntityMeta meta = dalEntityMetaManager.getEntity(obj.getClass());
		DalRdbmsEntity entity = DalRdbmsEntityManager.getInstance().build(meta);
		bindSequenceValues(obj, entity);
		SqlDmlDclMeta sqlDmlDclMeta = entity.getUpdateMeta(obj, schema);
		run(sqlDmlDclMeta);
	}

	private void bindSequenceValues(DalObj obj, DalRdbmsEntity entity) {
		if (dalSequence != null) {
			Set<String> seqFields = entity.getSeqFields();
			Set<String> seqGenerators = entity.getSeqGenerators();
			for (String seqField : seqFields) {
				Object v = get_v(obj, seqField);
				if (v == null) {
					v = dalSequence.next(obj, seqField);
					set_v(obj, seqField, v);
				}
			}
		}
	}

	public static Factory factory() {
		return new Factory();
	}

	public static class Factory extends DalKernel.Factory<DalRdbmsDmlRunner> {

		@Override
		protected DalRdbmsDmlRunner initRunner() {
			return new DalRdbmsDmlRunner();
		}

		public Factory dalSequence(DalSequenceSpi dalSequence) {
			runner.dalSequence = dalSequence;
			return this;
		}

	}

}
