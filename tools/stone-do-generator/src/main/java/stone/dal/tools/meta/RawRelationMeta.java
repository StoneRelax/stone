package stone.dal.tools.meta;

import stone.dal.common.models.meta.RelationMeta;
import stone.dal.common.models.meta.RelationTypes;

public class RawRelationMeta extends RelationMeta {
  private String joinDomain;

  private String joinColumnName;

  private String refColumn;

  private Boolean nullable = true;

  private Boolean updatable = true;

  protected String joinProperty;

  public String joinPropertyTypeName;

  private String joinTable;

  private RelationTypes relationType;

  public void setMappedBy(String mappedBy) {
    this.mappedBy = mappedBy;
  }

  public String getJoinProperty() {
    return joinProperty;
  }

  public String getJoinPropertyTypeName() {
    return joinPropertyTypeName;
  }

  public String getJoinTable() {
    return joinTable;
  }

  @Override
  public RelationTypes getRelationType() {
    return relationType;
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

  public void setJoinPropertyTypeName(String joinPropertyTypeName) {
    this.joinPropertyTypeName = joinPropertyTypeName;
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

  public String nullable() {
    return Boolean.toString(nullable);
  }

  public void setNullable(Boolean nullable) {
    this.nullable = nullable;
  }

  public String updatable() {
    return Boolean.toString(updatable);
  }

  public void setUpdatable(Boolean updatable) {
    this.updatable = updatable;
  }

  public void setJoinTable(String joinTable) {
    this.joinTable = joinTable;
  }
}
