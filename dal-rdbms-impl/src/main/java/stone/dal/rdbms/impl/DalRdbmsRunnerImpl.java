package stone.dal.rdbms.impl;

import org.springframework.stereotype.Component;
import stone.dal.common.api.DalObj;
import stone.dal.kernel.KernelRuntimeException;
import stone.dal.metadata.meta.Page;
import stone.dal.rdbms.api.DalRdbmsConstants;
import stone.dal.rdbms.api.DalRdbmsRunner;
import stone.dal.rdbms.api.meta.ExecResult;
import stone.dal.rdbms.api.meta.SqlCondition;
import stone.dal.rdbms.api.meta.SqlDmlDclMeta;
import stone.dal.rdbms.api.meta.SqlQueryMeta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static stone.dal.kernel.KernelUtils.str_2_arr;

/**
 * @author fengxie
 */
@Component
public class DalRdbmsRunnerImpl implements DalRdbmsRunner {

	private DalRdbmsQueryRunner queryRunner;
	private DalRdbmsDmlRunner dmlRunner;
	private DalRdbmsDclRunner dclRunner;

	public DalRdbmsRunnerImpl(DalRdbmsQueryRunner queryRunner,
							  DalRdbmsDmlRunner dmlRunner,
							  DalRdbmsDclRunner dclRunner) {
		this.queryRunner = queryRunner;
		this.dmlRunner = dmlRunner;
		this.dclRunner = dclRunner;
	}

	@Override
	public <T> List<T> runQuery(SqlQueryMeta queryMeta) {
		return queryRunner.run(queryMeta);
	}

	@Override
	public <T> Page<T> pagination(SqlQueryMeta queryMeta) {
		return queryRunner.runPagination(queryMeta);
	}

	@Override
	public int runDml(SqlDmlDclMeta meta) {
		return dmlRunner.run(meta);
	}

	@Override
	public int runDcl(String schema, String sql) {
		return dclRunner.run(schema, sql);
	}

	@Override
	public List<ExecResult> runSqlStream(InputStream inputStream) {
		return runSqlStream(inputStream, DalRdbmsConstants.PRIMARY_SCHEMA);
	}

	@Override
	public List<ExecResult> runSqlStream(InputStream inputStream, String schema) {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				if (!line.startsWith("--")) {
					sb.append(line);
				}
			}
			return runSqlScript(sb.toString(), schema);
		} catch (IOException e) {
			throw new KernelRuntimeException(e);
		}
	}

	@Override
	public List<ExecResult> runSqlScript(String sqlScripts, String dsSchema) {
		List<ExecResult> results = new ArrayList<>();
		String[] sqls = str_2_arr(sqlScripts, ";");
		for (String sql : sqls) {
			try {
				int rows = runDcl(dsSchema, sql);
				results.add(ExecResult.factory().sql(sql).rows(rows).build());
			} catch (Exception ex) {
				results.add(ExecResult.factory().sql(sql).error(ex.getMessage()).build());
			}
		}
		return results;
	}

	@Override
	public void runInsert(DalObj obj) {
		dmlRunner.runInsert(obj);
	}

	@Override
	public void runDelete(DalObj obj) {
		dmlRunner.runDelete(obj);
	}

	@Override
	public void runInsert(DalObj obj, String schema) {
		dmlRunner.runInsert(obj, schema);
	}

	@Override
	public void runUpdate(DalObj obj, String schema) {
		dmlRunner.runUpdate(obj, schema);
	}

	@Override
	public <T extends DalObj> T runFindOne(DalObj pk) {
		return queryRunner.runFind(pk);
	}

	@Override
	public <T extends DalObj> T runFindOne(DalObj pk, String schema) {
		return queryRunner.runFind(pk, schema);
	}

	@Override
	public <T extends DalObj> T runFindOne(SqlCondition condition, String schema) {
		return queryRunner.runFind(condition, schema);
	}

	@Override
	public <T> List<T> runFindMany(SqlCondition condition, String schema) {
		return queryRunner.runFind(condition, schema);
	}
}
