package stone.dal.tools.meta;

import stone.dal.models.meta.RelationTypes;

public class RawRelationMeta {
  private String joinDomain;
  private String joinColumnName;
  private String refColumn;
  private Boolean nullable;
  private Boolean updatable = true;

  protected String joinProperty;

  protected String joinPropertyType;
  private String joinTable;

  private RelationTypes relationType;

  public String getJoinProperty() {
    return joinProperty;
  }

  public String getJoinPropertyType() {
    return joinPropertyType;
  }

  public String getJoinTable() {
    return joinTable;
  }

  public void setRelationType(RelationTypes relationType) {
    this.relationType = relationType;
  }

  public String getJoinDomain() {
    return joinDomain;
  }

  public void setJoinDomain(String joinDomain) {
    this.joinDomain = joinDomain;
  }

  public void setJoinProperty(String joinProperty) {
    this.joinProperty = joinProperty;
  }

  public void setJoinPropertyType(String joinPropertyType) {
    this.joinPropertyType = joinPropertyType;
  }

  public String getJoinColumnName() {
    return joinColumnName;
  }

  public void setJoinColumnName(String joinColumnName) {
    this.joinColumnName = joinColumnName;
  }

  public String getRefColumn() {
    return refColumn;
  }

  public void setRefColumn(String refColumn) {
    this.refColumn = refColumn;
  }

  public Boolean getNullable() {
    return nullable;
  }

  public void setNullable(Boolean nullable) {
    this.nullable = nullable;
  }

  public Boolean getUpdatable() {
    return updatable;
  }

  public void setUpdatable(Boolean updatable) {
    this.updatable = updatable;
  }


  public void setJoinTable(String joinTable) {
    this.joinTable = joinTable;
  }
}
