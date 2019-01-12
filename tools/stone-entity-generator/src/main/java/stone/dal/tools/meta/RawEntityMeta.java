package stone.dal.tools.meta;

import java.util.ArrayList;
import java.util.List;
import stone.dal.models.meta.EntityMeta;

public class RawEntityMeta extends EntityMeta {
  private String name;
  private String label;
  private String delFlag;
  private String fileFieldTags;

  //  protected Collection<FieldMeta> fields;
  protected List<RawRelationMeta> rawRelations = new ArrayList<>();
//  protected Collection<UniqueIndexMeta> uniqueIndices;

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

}
