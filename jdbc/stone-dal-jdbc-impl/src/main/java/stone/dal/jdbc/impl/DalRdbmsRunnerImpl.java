package stone.dal.jdbc.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import stone.dal.jdbc.DalRdbmsDmlRunner;
import stone.dal.jdbc.DalRdbmsQueryRunner;
import stone.dal.jdbc.JdbcDclRunner;
import stone.dal.jdbc.api.DalRdbmsConstants;
import stone.dal.jdbc.api.DalRdbmsRunner;
import stone.dal.jdbc.api.meta.ExecResult;
import stone.dal.jdbc.api.meta.SqlDmlDclMeta;
import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.kernel.utils.KernelRuntimeException;
import stone.dal.models.data.BaseDo;
import stone.dal.models.data.Page;

import static stone.dal.kernel.utils.KernelUtils.str_2_arr;

/**
 * @author fengxie
 */
@Component
public class DalRdbmsRunnerImpl implements DalRdbmsRunner {

	private DalRdbmsQueryRunner queryRunner;
	private DalRdbmsDmlRunner dmlRunner;

	private JdbcDclRunner dclRunner;

	public DalRdbmsRunnerImpl(DalRdbmsQueryRunner queryRunner,
							  DalRdbmsDmlRunner dmlRunner,
			JdbcDclRunner dclRunner) {
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
	public void runInsert(BaseDo obj) {
		dmlRunner.runInsert(obj);
	}

	@Override
	public void runDelete(BaseDo obj) {
		dmlRunner.runDelete(obj);
	}

	@Override
	public <T extends BaseDo> T runFindOne(BaseDo pk) {
		return (T) queryRunner.runFind(pk);
	}

}
