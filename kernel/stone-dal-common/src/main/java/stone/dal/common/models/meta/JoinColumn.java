package stone.dal.common.models.meta;

/**
 * @author fengxie
 */
public class JoinColumn {
	private String name;
	private boolean nullable;
	private String referencedColumnName;
	private boolean updatable;
	private boolean unique;


	public JoinColumn(javax.persistence.JoinColumn joinColumn) {
		this.name = joinColumn.name();
		this.nullable = joinColumn.nullable();
		this.referencedColumnName = joinColumn.referencedColumnName();
		this.updatable = joinColumn.updatable();
		this.unique = joinColumn.unique();
	}

	public boolean isUpdatable() {
		return updatable;
	}

	public boolean isUnique() {
		return unique;
	}

	public String getName() {
		return name;
	}

	public boolean isNullable() {
		return nullable;
	}

	public String getReferencedColumnName() {
		return referencedColumnName;
	}
}
