package stone.dal.common.models.meta;

/**
 * @author fengxie
 */
public class IndexMeta {
	private String[] columnNames;
	private String name;

	private boolean unique;

	public IndexMeta(String[] columnNames, String name, boolean unique) {
		this.columnNames = columnNames;
		this.name = name;
		this.unique = unique;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public String getName() {
		return name;
	}

	public boolean isUnique() {
		return unique;
	}

}
