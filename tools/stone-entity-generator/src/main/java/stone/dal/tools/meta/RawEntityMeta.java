package stone.dal.tools.meta;

import stone.dal.models.meta.EntityMeta;

import java.util.ArrayList;
import java.util.List;

public class RawEntityMeta extends EntityMeta {
  private String name;
  private String label;
  private String dbName;
  private Boolean nosql;
  private String delFlag;
  private String fileFieldTags;

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

  public String getDbName() {
    return dbName;
  }

  public void setDbName(String dbName) {
    this.dbName = dbName;
  }

  public Boolean getNosql() {
    return nosql;
  }

  public void setNosql(Boolean nosql) {
    this.nosql = nosql;
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
