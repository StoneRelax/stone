package stone.dal.jdbc.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import stone.dal.jdbc.JdbcTemplateSpi;
import stone.dal.jdbc.api.StJdbcTemplate;
import stone.dal.jdbc.api.meta.ExecResult;
import stone.dal.jdbc.api.meta.SqlDmlDclMeta;
import stone.dal.jdbc.api.meta.SqlQueryMeta;
import stone.dal.kernel.utils.KernelRuntimeException;
import stone.dal.models.data.Page;

import static stone.dal.kernel.utils.KernelUtils.str2Arr;

/**
 * @author fengxie
 */
@Component
public class StJdbcTemplateImpl implements StJdbcTemplate {

	private JdbcTemplateSpi jdbcTemplateSpi;

	public StJdbcTemplateImpl(JdbcTemplateSpi jdbcTemplateSpi) {
		this.jdbcTemplateSpi = jdbcTemplateSpi;
	}

	@Override
	public <T> List<T> runQuery(SqlQueryMeta queryMeta) {
		return jdbcTemplateSpi.run(queryMeta);
	}

	@Override
	public <T> Page<T> pagination(SqlQueryMeta queryMeta) {
		return jdbcTemplateSpi.runPagination(queryMeta);
	}

	@Override
	public int runDml(SqlDmlDclMeta meta) {
		return jdbcTemplateSpi.exec(meta);
	}

	@Override
	public int runDcl(String sql) {
		return jdbcTemplateSpi.exec(SqlDmlDclMeta.factory().sql(sql).build());
	}

	@Override
	public List<ExecResult> runSqlStream(InputStream inputStream) {
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
			return runSqlScript(sb.toString());
		} catch (IOException e) {
			throw new KernelRuntimeException(e);
		}
	}

	@Override
	public List<ExecResult> runSqlScript(String sqlScripts) {
		List<ExecResult> results = new ArrayList<>();
    String[] sqls = str2Arr(sqlScripts, ";");
		for (String sql : sqls) {
			try {
				int rows = runDcl(sql);
				results.add(ExecResult.factory().sql(sql).rows(rows).build());
			} catch (Exception ex) {
				results.add(ExecResult.factory().sql(sql).error(ex.getMessage()).build());
			}
		}
		return results;
	}

//  @Override
//  public <T extends BaseDo> T runFind(SqlCondition condition) {
//    List list = runFindMany(condition);
//    if (!isCollectionEmpty(list)) {
//      return (T) list.iterator().next();
//    }
//    return null;
//  }
//
//  public <T> List<T> runFindMany(SqlCondition condition) {
//		SqlQueryMeta queryMeta = condition.build();
//		EntityMeta meta = dalEntityMetaManager.getEntity(queryMeta.getMappingClazz());
//		RdbmsEntity entity = RdbmsEntityManager.getInstance().build(meta);
//		String sql = entity.getFindSqlNoCondition() + " where ";
//		SqlQueryMeta _queryMeta = SqlQueryMeta.factory()
//				.mappingClazz(queryMeta.getMappingClazz())
//				.sql(sql).join(queryMeta).build();
//		return exec(_queryMeta);
//    return null;
//  }
}
