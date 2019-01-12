/*
 * File: $RCSfile: RawFieldMeta.java,v $
 *
 * Copyright (c) 2008 Dr0ne.Dev Studio
 */
package stone.dal.tools.meta;

import stone.dal.models.meta.FieldMeta;

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

  public void setSeqKey(String seqKey) {
    this.seqKey = seqKey;
  }

  public void setSeqType(String seqType) {
    this.seqType = seqType;
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

  public void setMapper(String mapper) {
    this.mapper = mapper;
  }

  public void setMappedBy(String mappedBy) {
    this.mappedBy = mappedBy;
  }

  public void setOrder(String order) {
    this.order = order;
  }

  public void setGroupByAllowed(Boolean groupByAllowed) {
    this.groupByAllowed = groupByAllowed;
  }

  public void setNotPersist(Boolean notPersist) {
    this.notPersist = notPersist;
  }

  public void setIndex(String index) {
    this.index = index;
  }

  public void setWriteWhenNotEmpty(Boolean writeWhenNotEmpty) {
    this.writeWhenNotEmpty = writeWhenNotEmpty;
  }

  public void setFile(Boolean file) {
    this.file = file;
  }

  public void setConstraints(String constraints) {
    this.constraints = constraints;
  }

  public void setClob(Boolean clob) {
    this.clob = clob;
  }

  public void setUpdatable(Boolean updatable) {
    this.updatable = updatable;
  }
}
