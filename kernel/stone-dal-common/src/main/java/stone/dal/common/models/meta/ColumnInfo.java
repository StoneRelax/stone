package stone.dal.common.models.meta;

import java.util.Objects;

public class ColumnInfo {
  private String field;

  private boolean nullable;

  private DBType type;

  private boolean isPk;

  private String property;

  public ColumnInfo(String field, boolean nullable, DBType type, String property, boolean isPk) {
    this.field = field;
    this.nullable = nullable;
    this.type = type;
    this.isPk = isPk;
    this.property = property;
  }

  public String getProperty() {
    return property;
  }

  public String getField() {
    return field;
  }

  public boolean getNullable() {
    return nullable;
  }

  public DBType getType() {
    return type;
  }

  public boolean getPk() {
    return isPk;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ColumnInfo that = (ColumnInfo) o;
    return nullable == that.nullable &&
        Objects.equals(field, that.field) &&
        type == that.type &&
        Objects.equals(property, that.property);
  }

  @Override
  public int hashCode() {
    return Objects.hash(field, nullable, type, property);
  }

  public enum DBType {
    varchar,
    decimal,
    date,
    datetime
  }
}
