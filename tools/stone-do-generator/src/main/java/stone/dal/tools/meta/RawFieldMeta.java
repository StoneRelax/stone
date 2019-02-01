/*
 * File: $RCSfile: RawFieldMeta.java,v $
 *
 * Copyright (c) 2008 Dr0ne.Dev Studio
 */
package stone.dal.tools.meta;

import stone.dal.common.models.meta.FieldMeta;
import stone.dal.kernel.utils.StringUtils;

/**
 * Component scope: JBolt Data Dictionary
 * Responsibilities: Data field meta
 *
 * @author feng.xie, Dr0ne.Dev Studio
 * @version $Revision: 1.2 $
 */
public class RawFieldMeta extends FieldMeta {

  private String typeName;

  private String unique;

  private String seqDesc;

  private String mapperDesc;

  private String fieldProperty;

  private String label;

  private Boolean notNull = false;

  private String remarks;

  public String getRemarks() {
    return remarks;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  public String getDbName() {
    if (StringUtils.isEmpty(dbName)) {
      return StringUtils.canonicalPropertyName2DBField(name);
    }
    return dbName;
  }

  @Override
  public Boolean getNullable() {
    return !notNull;
  }

  public Boolean getNotNull() {
    return notNull;
  }

  public void setNotNull(Boolean notNull) {
    this.notNull = notNull;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getFieldProperty() {
    return fieldProperty;
  }

  public void setFieldProperty(String fieldProperty) {
    this.fieldProperty = fieldProperty;
  }

  public void setMapperBy(String mapperBy) {
    this.mappedBy = mapperBy;
  }

  public void setMapper(String mapper) {
    this.mapper = mapper;
  }

  public String getMapperDesc() {
    return mapperDesc;
  }

  public void setMapperDesc(String mapperDesc) {
    this.mapperDesc = mapperDesc;
  }

  public String getUnique() {
    return unique;
  }

  public void setUnique(String unique) {
    this.unique = unique;
  }

  public String getTypeName() {
    return typeName;
  }

  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  public void setSeqType(String seqType) {
    this.seqType = seqType;
  }

  public String getSeqDesc() {
    return seqDesc;
  }

  public void setSeqDesc(String seqDesc) {
    this.seqDesc = seqDesc;
  }

  public void setSeqStartNum(int defaultStartSeq) {
    this.seqStartNum = defaultStartSeq;
  }

  public void setDbName(String dbName) {
    this.dbName = dbName;
  }

  public void setNullable(Boolean nullable) {
    this.nullable = nullable;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPk(Boolean pk) {
    this.pk = pk;
  }

  public void setScale(Integer scale) {
    this.scale = scale;
  }

  public void setPrecision(Integer precision) {
    this.precision = precision;
  }

  public void setOrder(String order) {
    this.order = order;
  }

  public void setNotPersist(Boolean notPersist) {
    this.notPersist = notPersist;
  }

  public void setIndex(String index) {
    this.index = index;
  }

  public void setFile(Boolean file) {
    this.file = file;
  }

  public void setClob(Boolean clob) {
    this.clob = clob;
  }

  public void setUpdatable(Boolean updatable) {
    this.updatable = updatable;
  }

}
