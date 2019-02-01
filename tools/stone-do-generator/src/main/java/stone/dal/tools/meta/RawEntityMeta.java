package stone.dal.tools.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import stone.dal.common.models.meta.EntityMeta;
import stone.dal.common.models.meta.FieldMeta;
import stone.dal.common.models.meta.UniqueIndexMeta;

public class RawEntityMeta extends EntityMeta {

  private String name;

  private String label;

  private String delFlag;

  private String clazzName;

  private List<RawFieldMeta> fields = new ArrayList<>();

  private List<EntityListener> entityListeners = new ArrayList<>();

  protected List<RawRelationMeta> rawRelations = new ArrayList<>();

  protected Collection<UniqueIndexMeta> uniqueIndices = new ArrayList<>();

  public List<RawRelationMeta> getRawRelations() {
    return rawRelations;
  }

  public void setRawRelations(List<RawRelationMeta> rawRelations) {
    this.rawRelations = rawRelations;
  }

  public void setNosql(boolean nosql) {
    this.nosql = nosql;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getDelFlag() {
    return delFlag;
  }

  public void setDelFlag(String delFlag) {
    this.delFlag = delFlag;
  }

  public List<RawFieldMeta> getRawFields() {
    return fields;
  }

  public void setRawFields(List<RawFieldMeta> fields) {
    this.fields = fields;
  }

  public HashSet<String> pks() {
    return (HashSet<String>) fields.stream().filter(FieldMeta::getPk).map(FieldMeta::getName)
        .collect(Collectors.toSet());
  }

  public String getClazzName() {
    return clazzName;
  }

  public void setClazzName(String clazzName) {
    this.clazzName = clazzName;
  }

  @Override
  public Collection<UniqueIndexMeta> getUniqueIndices() {
    return uniqueIndices;
  }

  public List<EntityListener> getEntityListeners() {
    return entityListeners;
  }

  public void setEntityListeners(List<EntityListener> entityListeners) {
    this.entityListeners = entityListeners;
  }
}
