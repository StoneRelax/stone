package stone.dal.common.models.meta;

/**
 * @author fengxie
 */
public class UniqueIndexMeta {
	private String[] columnNames;
	private String name;

	public UniqueIndexMeta(String[] columnNames, String name) {
		this.columnNames = columnNames;
		this.name = name;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
