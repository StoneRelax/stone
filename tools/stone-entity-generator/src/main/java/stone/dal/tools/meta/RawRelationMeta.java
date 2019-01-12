package stone.dal.tools.meta;

import stone.dal.models.meta.RelationMeta;

public class RawRelationMeta extends RelationMeta {
  private String relationType;
  private String joinDomain;
  private String joinProperty;
  private String joinPropertyType;
  private String joinColumnName;
  private String refColumn;
  private Boolean nullable;
  private Boolean updatable = true;
  private String mapperBy;
  private String joinTable;
  private Boolean adhoc;


  public void setRelationType(String relationType) {
    this.relationType = relationType;
  }

  public String getJoinDomain() {
    return joinDomain;
  }

  public void setJoinDomain(String joinDomain) {
    this.joinDomain = joinDomain;
  }

  @Override
  public String getJoinProperty() {
    return joinProperty;
  }

  public void setJoinProperty(String joinProperty) {
    this.joinProperty = joinProperty;
  }

  @Override
  public String getJoinPropertyType() {
    return joinPropertyType;
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

  @Override
  public String getMapperBy() {
    return mapperBy;
  }

  public void setMapperBy(String mapperBy) {
    this.mapperBy = mapperBy;
  }

  @Override
  public String getJoinTable() {
    return joinTable;
  }

  public void setJoinTable(String joinTable) {
    this.joinTable = joinTable;
  }

  public Boolean getAdhoc() {
    return adhoc;
  }

  public void setAdhoc(Boolean adhoc) {
    this.adhoc = adhoc;
  }
}
