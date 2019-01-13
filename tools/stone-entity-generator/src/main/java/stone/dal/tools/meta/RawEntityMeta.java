package stone.dal.tools.meta;

import stone.dal.models.meta.EntityMeta;
import stone.dal.models.meta.FieldMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RawEntityMeta extends EntityMeta {

  private String name;

  private String label;

  private String delFlag;

  private String fileFieldTags;

  private String clazzName;

  private List<FieldMeta> fields = new ArrayList<>();

  protected List<RawRelationMeta> rawRelations = new ArrayList<>();

  private transient HashSet<String> pks = new HashSet<String>();

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

  public String getFileFieldTags() {
    return fileFieldTags;
  }

  public void setFileFieldTags(String fileFieldTags) {
    this.fileFieldTags = fileFieldTags;
  }

  @Override
  public List<FieldMeta> getFields() {
    return fields;
  }

  public void setFields(List<FieldMeta> fields) {
    this.fields = fields;
  }

  public HashSet<String> pks() {
    return pks;
  }

  public String getClazzName() {
    return clazzName;
  }

  public void setClazzName(String clazzName) {
    this.clazzName = clazzName;
  }
}
