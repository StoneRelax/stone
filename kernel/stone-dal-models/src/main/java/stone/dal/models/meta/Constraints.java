package stone.dal.models.meta;

/**
 * @author fengxie
 */
public class Constraints {
	private String refBy;
	private String field;

	public Constraints(String refBy, String field) {
		this.refBy = refBy;
		this.field = field;
	}

	public String getRefBy() {
		return refBy;
	}

	public String getField() {
		return field;
	}
}
