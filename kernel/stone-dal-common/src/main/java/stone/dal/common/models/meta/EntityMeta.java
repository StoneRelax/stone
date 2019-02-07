package stone.dal.common.models.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityListeners;

/**
 * @author fengxie
 */
public abstract class EntityMeta {
  protected String tableName;

  protected boolean nosql;

  transient Class clazz;

  transient Class pkClazz;

  protected Collection<FieldMeta> fields;

  protected Collection<RelationMeta> relations;

  protected Collection<IndexMeta> uniqueIndices;

  protected Collection<IndexMeta> indices;

  private Collection<Class> entityListenersClasses;

  public boolean isNosql() {
    return nosql;
  }

  public String getTableName() {
    return tableName;
  }

  public Class getClazz() {
    return clazz;
  }

  public Class getPkClazz() {
    return pkClazz;
  }

  public Collection<FieldMeta> getFields() {
    return fields;
  }

  public Collection<RelationMeta> getRelations() {
    return relations;
  }

  public Collection<Class> getEntityListenersClasses() {
    return entityListenersClasses;
  }

  public Collection<IndexMeta> getUniqueIndices() {
    return uniqueIndices;
  }

  public Collection<IndexMeta> getIndices() {
    return indices;
  }

  public static Factory factory() {
    return new Factory();
  }

  public static class Factory {

    private List<FieldMeta> fields = new ArrayList<>();

    private List<RelationMeta> relations = new ArrayList<>();

    private List<IndexMeta> indicies = new ArrayList<>();

    private List<Class> entityListenersClasses = new ArrayList<>();

    private EntityMeta meta = new EntityMeta() {
    };

    public Factory tableName(String tableName) {
      meta.tableName = tableName;
      return this;
    }

    public Factory nosql(boolean nosql) {
      meta.nosql = nosql;
      return this;
    }

    public Factory clazz(Class clazz) {
      meta.clazz = clazz;
      return this;
    }

    public Factory addField(FieldMeta field) {
      fields.add(field);
      return this;
    }

    public Factory addRelation(RelationMeta relationMeta) {
      relations.add(relationMeta);
      return this;
    }

    public Factory addIndex(IndexMeta indexMeta) {
      indicies.add(indexMeta);
      return this;
    }

    public Factory addEntityListeners(EntityListeners listeners) {
      entityListenersClasses.addAll(Arrays.asList(listeners.value()));
      return this;
    }

    public EntityMeta build() {
      meta.fields = Collections.unmodifiableCollection(fields);
      meta.relations = Collections.unmodifiableCollection(relations);
      meta.uniqueIndices = Collections.unmodifiableCollection(indicies.stream().filter(IndexMeta::isUnique).collect(
          Collectors.toList()));
      meta.indices = Collections
          .unmodifiableCollection(indicies.stream().filter(indexMeta -> !indexMeta.isUnique()).collect(
              Collectors.toList()));
      meta.entityListenersClasses = Collections.unmodifiableCollection(entityListenersClasses);
      return meta;
    }
  }
}
