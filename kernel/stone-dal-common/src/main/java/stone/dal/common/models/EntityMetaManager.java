package stone.dal.common.models;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stone.dal.common.ex.DoParseException;
import stone.dal.common.models.annotation.Clob;
import stone.dal.common.models.annotation.FileField;
import stone.dal.common.models.annotation.Sequence;
import stone.dal.common.models.annotation.UniqueIndex;
import stone.dal.common.models.annotation.UniqueIndices;
import stone.dal.common.models.meta.EntityMeta;
import stone.dal.common.models.meta.FieldMeta;
import stone.dal.common.models.meta.RelationMeta;
import stone.dal.common.models.meta.RelationTypes;
import stone.dal.common.models.meta.UniqueIndexMeta;
import stone.dal.kernel.utils.ClassUtils;
import stone.dal.kernel.utils.LogUtils;
import stone.dal.kernel.utils.StringUtils;
import stone.dal.kernel.utils.UrlUtils;

import static stone.dal.kernel.utils.KernelUtils.isArrayEmpty;

/**
 * @author fengxie
 */
public class EntityMetaManager {

  private static Logger logger = LoggerFactory.getLogger(EntityMetaManager.class);

  private Map<String, EntityMeta> entityMapper = new HashMap<>();

  public EntityMetaManager(String[] scanPackages) throws DoParseException {
    try {
      for (String packageName : scanPackages) {
        Set<Class> classes = UrlUtils.findClassesByPackage(packageName);
        for (Class clazz : classes) {
          parseClass(clazz);
        }
      }
    } catch (Exception ex) {
      logger.error(LogUtils.printEx(ex));
      throw new DoParseException(ex);
    }
  }

  public EntityMeta getEntityByClazzName(String name) {
    return entityMapper.get(name);
  }

  public EntityMeta getEntity(Class clazz) {
    return entityMapper.get(org.springframework.util.ClassUtils.getUserClass(clazz).getName());
  }

  public Set<EntityMeta> getAllEntities() {
    return entityMapper.keySet().stream().map(key -> entityMapper.get(key)).collect(Collectors.toSet());
  }

  @SuppressWarnings("unchecked")
  private void parseClass(Class dalClazz) throws ClassNotFoundException {
    Class clazz = org.springframework.util.ClassUtils.getUserClass(dalClazz);
    if (clazz.isAnnotationPresent(Entity.class)) {
      EntityMeta.Factory entityFactory = EntityMeta.factory();
      entityFactory.clazz(clazz);
      if (clazz.isAnnotationPresent(Table.class)) {
        Table tableAnnotation = (Table) dalClazz.getAnnotation(Table.class);
        entityFactory.tableName(tableAnnotation.name());
        parseFields(clazz, entityFactory);
      }
      if (clazz.isAnnotationPresent(EntityListeners.class)) {
        EntityListeners listeners = (EntityListeners) dalClazz.getAnnotation(EntityListeners.class);
        entityFactory.addEntityListeners(listeners);
      }
      if (clazz.isAnnotationPresent(UniqueIndices.class)) {
        UniqueIndices uniqueIndices = (UniqueIndices) dalClazz.getAnnotation(UniqueIndices.class);
        UniqueIndex[] indices = uniqueIndices.indices();
        if (!isArrayEmpty(indices)) {
          for (UniqueIndex index : indices) {
            UniqueIndexMeta indexMeta = new UniqueIndexMeta(index.columnNames(), index.name());
            entityFactory.addUniqueMeta(indexMeta);
          }
        }
      }
      parseRelation(dalClazz, entityFactory);
      EntityMeta entityMeta = entityFactory.build();
      entityMapper.put(org.springframework.util.ClassUtils.getUserClass(clazz).getName(), entityMeta);
    }
  }

  private void parseFields(Class clazz, EntityMeta.Factory entityFactory) {
    PropertyDescriptor[] descriptors = org.springframework.beans.BeanUtils.getPropertyDescriptors(clazz);
    for (PropertyDescriptor propertyDesc : descriptors) {
      String propertyName = propertyDesc.getName();
      if (!propertyName.startsWith("_")) {
        Class propertyType = propertyDesc.getPropertyType();
        if (ClassUtils.isPrimitive(propertyType) || propertyType.isEnum()) {
          Method readMethod = propertyDesc.getReadMethod();
          Column column = readMethod.getAnnotation(Column.class);
          FieldMeta.Factory fieldFactory = FieldMeta.factory();
          if (column != null) {
            fieldFactory
                .dbName(column.name())
                .maxLength(column.length())
                .precision(column.precision())
                .scale(column.scale())
                .nullable(column.nullable())
                .insertable(column.insertable())
                .updatable(column.updatable());
          } else {
            fieldFactory.notPersist(readMethod.isAnnotationPresent(Transient.class));
          }
          fieldFactory.name(propertyName)
              .pk(readMethod.isAnnotationPresent(Id.class))
              .type(propertyType)
              .file(readMethod.isAnnotationPresent(FileField.class))
              .clob(readMethod.isAnnotationPresent(Clob.class));
          if (readMethod.isAnnotationPresent(OrderBy.class)) {
            OrderBy orderBy = readMethod.getAnnotation(OrderBy.class);
            fieldFactory.order(orderBy.value());
          }
          parseSequence(readMethod, fieldFactory, clazz.getSimpleName(), propertyName);
          entityFactory.addField(fieldFactory.build());
        }
      }
    }
  }

  private void parseRelation(Class clazz, EntityMeta.Factory entityFactory) throws ClassNotFoundException {
    PropertyDescriptor[] descriptors = org.springframework.beans.BeanUtils.getPropertyDescriptors(clazz);
    for (PropertyDescriptor propertyDesc : descriptors) {
      Method readMethod = propertyDesc.getReadMethod();
      RelationMeta.Factory factory = new RelationMeta.Factory();
      if (readMethod.isAnnotationPresent(OneToMany.class)) {
        OneToMany one2Many = readMethod.getAnnotation(OneToMany.class);
        factory.mappedBy(one2Many.mappedBy()).relationType(RelationTypes.ONE_2_MANY);
        factory.joinProperty(propertyDesc.getName());
        parseOrder(factory, readMethod);
        factory.joinPropertyType(getRelationClazz(readMethod, clazz));
        entityFactory.addRelation(factory.build());
      } else if (readMethod.isAnnotationPresent(ManyToMany.class)) {
        JoinTable joinTable = readMethod.getAnnotation(JoinTable.class);
        JoinColumn[] inverseJoinColumns = joinTable.inverseJoinColumns();
        JoinColumn[] joinColumns = joinTable.joinColumns();
        factory.relationType(RelationTypes.MANY_2_MANY);
        for (JoinColumn joinColumn : joinColumns) {
          factory.addJoinColumn(new stone.dal.common.models.meta.JoinColumn(joinColumn));
        }
        for (JoinColumn joinColumn : inverseJoinColumns) {
          factory.addInverseJoinColumn(new stone.dal.common.models.meta.JoinColumn(joinColumn));
        }
        factory.joinTable(joinTable.name());
        parseOrder(factory, readMethod);
        factory.joinProperty(propertyDesc.getName());
        factory.joinPropertyType(getRelationClazz(readMethod, clazz));
        entityFactory.addRelation(factory.build());
      } else if (readMethod.isAnnotationPresent(ManyToOne.class)) {
        if (readMethod.isAnnotationPresent(ManyToOne.class)) {
          factory.relationType(RelationTypes.MANY_2_ONE);
          if (readMethod.isAnnotationPresent(JoinColumn.class)) {
            JoinColumn joinColumn = readMethod.getAnnotation(JoinColumn.class);
            factory.addJoinColumn(new stone.dal.common.models.meta.JoinColumn(joinColumn));
          } else if (readMethod.isAnnotationPresent(JoinColumns.class)) {
            JoinColumns joinColumns = readMethod.getAnnotation(JoinColumns.class);
            for (JoinColumn _joinColumn : joinColumns.value()) {
              factory.addJoinColumn(new stone.dal.common.models.meta.JoinColumn(_joinColumn));
            }
          }
        }
        factory.joinProperty(propertyDesc.getName());
        factory.joinPropertyType(getRelationClazz(readMethod, clazz));
        parseOrder(factory, readMethod);
        entityFactory.addRelation(factory.build());
      } else if (readMethod.isAnnotationPresent(OneToOne.class)) {
        if (readMethod.isAnnotationPresent(JoinColumn.class)) {
          JoinColumn joinColumn = readMethod.getAnnotation(JoinColumn.class);
          factory.addJoinColumn(new stone.dal.common.models.meta.JoinColumn(joinColumn));
          if (joinColumn.updatable()) {
            factory.relationType(RelationTypes.ONE_2_ONE_VAL);
          } else {
            factory.relationType(RelationTypes.ONE_2_ONE_REF);
          }
        } else if (readMethod.isAnnotationPresent(JoinColumns.class)) {
          JoinColumns joinColumns = readMethod.getAnnotation(JoinColumns.class);
          for (JoinColumn _joinColumn : joinColumns.value()) {
            factory.addJoinColumn(new stone.dal.common.models.meta.JoinColumn(_joinColumn));
          }
          factory.relationType(RelationTypes.ONE_2_ONE_VAL);
        }
        factory.joinPropertyType(getRelationClazz(readMethod, clazz));
        parseOrder(factory, readMethod);
        entityFactory.addRelation(factory.build());
      }
    }
  }

  private void parseOrder(RelationMeta.Factory factory, Method readMethod) {
    if (readMethod.isAnnotationPresent(OrderBy.class)) {
      OrderBy orderBy = readMethod.getAnnotation(OrderBy.class);
      factory.fetchOrder(orderBy.value());
    }
  }

  private void parseSequence(Method readMethod, FieldMeta.Factory fieldFactory, String entityName, String fieldName) {
    if (readMethod.isAnnotationPresent(Sequence.class)) {
      Sequence seq = readMethod.getAnnotation(Sequence.class);
      fieldFactory.seqType(seq.generator());
      String key = seq.key();
      if (StringUtils.isEmpty(key)) {
        key = entityName + "." + fieldName;
      }
      fieldFactory.seqKey(key).defaultStartSeq(seq.defaultStartSeq());
    }
  }

  private Class getRelationClazz(Method readMethod, Class objClazz) throws ClassNotFoundException {
    Class returnType = readMethod.getReturnType();
    Class relatedObjClazz;
    if (org.springframework.util.ClassUtils.isAssignable(Collection.class, returnType)) {
      String propertyName = StringUtils.getPropertyNameByMethod(readMethod);
      relatedObjClazz = ClassUtils.getCollectionType(objClazz, propertyName);
    } else {
      relatedObjClazz = returnType;
    }
    return relatedObjClazz;
  }

}
