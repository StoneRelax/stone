package stone.dal.rdbms.api.meta;

/**
 * @author fengxie
 */
public class ExecResult {
	private String error;
	private int rows;
	private String sql;

	public String getSql() {
		return sql;
	}

	public int getRows() {
		return rows;
	}

	public String getError() {
		return error;
	}

	public static Factory factory() {
		return new Factory();
	}

	public static class Factory {
		private ExecResult result = new ExecResult();

		public Factory error(String error) {
			result.error = error;
			return this;
		}

		public Factory sql(String sql) {
			result.sql = sql;
			return this;
		}

		public Factory rows(int rows) {
			result.rows = rows;
			return this;
		}

		public ExecResult build() {
			return result;
		}
	}
}
