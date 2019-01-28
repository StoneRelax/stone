package stone.dal.tools.meta;

/**
 * Description:
 * Author: Thinkpad on 2016/1/15..
 */
public class FieldConstraints {
  private String refBy;

  private String field;

  public String getRefBy() {
    return refBy;
  }

  public void setRefBy(String refBy) {
    this.refBy = refBy;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    FieldConstraints that = (FieldConstraints) o;

    if (refBy != null ? !refBy.equals(that.refBy) : that.refBy != null)
      return false;
    return !(field != null ? !field.equals(that.field) : that.field != null);

  }

  @Override
  public int hashCode() {
    int result = refBy != null ? refBy.hashCode() : 0;
    result = 31 * result + (field != null ? field.hashCode() : 0);
    return result;
  }
}
