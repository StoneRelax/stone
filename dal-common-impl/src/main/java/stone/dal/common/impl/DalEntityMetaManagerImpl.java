package stone.dal.common.impl;

import stone.dal.common.api.DalEntityMetaManager;
import stone.dal.common.api.RelationTypes;
import stone.dal.common.api.annotation.*;
import stone.dal.common.api.meta.EntityMeta;
import stone.dal.common.api.meta.FieldMeta;
import stone.dal.common.api.meta.RelationMeta;
import stone.dal.common.api.meta.UniqueIndexMeta;
import stone.dal.metadata.meta.PlatformRuntimeException;
import stone.dal.kernel.ClassUtils;
import stone.dal.kernel.LogUtils;
import stone.dal.kernel.StringUtils;
import stone.dal.kernel.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static stone.dal.kernel.KernelUtils.arr_emp;

/**
 * @author fengxie
 */
public class DalEntityMetaManagerImpl implements DalEntityMetaManager {

	private static Logger logger = LoggerFactory.getLogger(DalEntityMetaManagerImpl.class);
	protected Map<String, EntityMeta> entityMapper = new HashMap<>();

	public DalEntityMetaManagerImpl() throws PlatformRuntimeException {
		try {
			init();
		} catch (Exception e) {
			LogUtils.error(logger, e);
			throw new PlatformRuntimeException(e);
		}
	}

	private void init() throws Exception {
		String[] packages = DalConfigManager.getInstance().getScanPackages();
		for (String packageName : packages) {
			Set<Class> classes = UrlUtils.findClassesByPackage(packageName);
			classes.forEach(this::parseClass);
		}
	}

	public EntityMeta getEntityByClazzName(String name) {
		return entityMapper.get(name);
	}

	@Override
	public EntityMeta getEntity(Class clazz) {
		return entityMapper.get(org.springframework.util.ClassUtils.getUserClass(clazz).getName());
	}

	@SuppressWarnings("unchecked")
	private void parseClass(Class dalClazz) {
		Class clazz = org.springframework.util.ClassUtils.getUserClass(dalClazz);
		if (clazz.isAnnotationPresent(Entity.class)) {
			EntityMeta.Factory entityFactory = EntityMeta.factory();
			entityFactory.clazz(clazz);
			if (clazz.isAnnotationPresent(Table.class)) {
				Table tableAnnotation = (Table) dalClazz.getAnnotation(Table.class);
				entityFactory.tableName(tableAnnotation.name());
				parseFields(clazz, entityFactory);
			}
			if (clazz.isAnnotationPresent(UniqueIndices.class)) {
				UniqueIndices uniqueIndices = (UniqueIndices) dalClazz.getAnnotation(UniqueIndices.class);
				UniqueIndex[] indices = uniqueIndices.indices();
				if (!arr_emp(indices)) {
					for (UniqueIndex index : indices) {
						UniqueIndexMeta indexMeta = new UniqueIndexMeta(index.columnNames(), index.name());
						entityFactory.addUniqueMeta(indexMeta);
					}
				}
			}
			if (clazz.isAnnotationPresent(Cache.class)) {
				String[] keys = ((Cache) dalClazz.getAnnotation(Cache.class)).keys();
				for (String key : keys) {
					entityFactory.addCacheKey(key);
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
							.clob(readMethod.isAnnotationPresent(Clob.class))
							.groupByAllowed(readMethod.isAnnotationPresent(GroupByAllowed.class))
							.i18n(readMethod.isAnnotationPresent(I18n.class))
							.hideWhenQuery(readMethod.isAnnotationPresent(HideWhenQuery.class));
					if (readMethod.isAnnotationPresent(OrderBy.class)) {
						OrderBy orderBy = readMethod.getAnnotation(OrderBy.class);
						fieldFactory.order(orderBy.value());
					}
					parseSequence(readMethod, fieldFactory);
					entityFactory.addField(fieldFactory.build());
				}
			}
		}
	}

	//	private void readOtherInfo(Method readMethod, FieldMeta field) {
//		if (readMethod.isAnnotationPresent(FieldMapper.class)) {
//			FieldMapper dataFieldMapper = readMethod.getAnnotation(FieldMapper.class);
//			field.setMapper(dataFieldMapper.mapper());
//			field.setMapperArgs(dataFieldMapper.args());
//			field.setMappedBy(dataFieldMapper.mappedBy());
//		}
//	}
//
	private void parseRelation(Class clazz, EntityMeta.Factory entityFactory) {
		PropertyDescriptor[] descriptors = org.springframework.beans.BeanUtils.getPropertyDescriptors(clazz);
		for (PropertyDescriptor propertyDesc : descriptors) {
			Method readMethod = propertyDesc.getReadMethod();
			RelationMeta.Factory factory = new RelationMeta.Factory();
			if (readMethod.isAnnotationPresent(OneToMany.class)) {
				OneToMany one2Many = readMethod.getAnnotation(OneToMany.class);
				factory.mapperBy(one2Many.mappedBy()).relationType(RelationTypes.ONE_2_MANY);
				factory.joinProperty(propertyDesc.getName());
				parseOrder(factory, readMethod);
				factory.joinPropertyType(getRelationClazz(readMethod, clazz).getName());
				entityFactory.addRelation(factory.build());
			} else if (readMethod.isAnnotationPresent(ManyToMany.class)) {
				JoinTable joinTable = readMethod.getAnnotation(JoinTable.class);
				JoinColumn[] inverseJoinColumns = joinTable.inverseJoinColumns();
				JoinColumn[] joinColumns = joinTable.inverseJoinColumns();
				factory.relationType(RelationTypes.MANY_2_MANY);
				for (JoinColumn joinColumn : joinColumns) {
					factory.addJoinColumn(new stone.dal.common.api.meta.JoinColumn(joinColumn));
				}
				for (JoinColumn joinColumn : inverseJoinColumns) {
					factory.addInverseJoinColumn(new stone.dal.common.api.meta.JoinColumn(joinColumn));
				}
				parseOrder(factory, readMethod);
				factory.joinProperty(propertyDesc.getName());
				factory.joinPropertyType(getRelationClazz(readMethod, clazz).getName());
				entityFactory.addRelation(factory.build());
			} else if (readMethod.isAnnotationPresent(ManyToOne.class)) {
				if (readMethod.isAnnotationPresent(ManyToOne.class)) {
					factory.relationType(RelationTypes.MANY_2_ONE);
					if (readMethod.isAnnotationPresent(JoinColumn.class)) {
						JoinColumn joinColumn = readMethod.getAnnotation(JoinColumn.class);
						factory.addJoinColumn(new stone.dal.common.api.meta.JoinColumn(joinColumn));
					} else if (readMethod.isAnnotationPresent(JoinColumns.class)) {
						JoinColumns joinColumns = readMethod.getAnnotation(JoinColumns.class);
						for (JoinColumn _joinColumn : joinColumns.value()) {
							factory.addJoinColumn(new stone.dal.common.api.meta.JoinColumn(_joinColumn));
						}
					}
				}
				factory.joinProperty(propertyDesc.getName());
				factory.joinPropertyType(getRelationClazz(readMethod, clazz).getName());
				parseOrder(factory, readMethod);
				entityFactory.addRelation(factory.build());
			} else if (readMethod.isAnnotationPresent(OneToOne.class)) {
				if (readMethod.isAnnotationPresent(JoinColumn.class)) {
					JoinColumn joinColumn = readMethod.getAnnotation(JoinColumn.class);
					factory.addJoinColumn(new stone.dal.common.api.meta.JoinColumn(joinColumn));
					if (joinColumn.updatable()) {
						factory.relationType(RelationTypes.ONE_2_ONE_VAL);
					} else {
						factory.relationType(RelationTypes.ONE_2_ONE_REF);
					}
				} else if (readMethod.isAnnotationPresent(JoinColumns.class)) {
					JoinColumns joinColumns = readMethod.getAnnotation(JoinColumns.class);
					for (JoinColumn _joinColumn : joinColumns.value()) {
						factory.addJoinColumn(new stone.dal.common.api.meta.JoinColumn(_joinColumn));
					}
					factory.relationType(RelationTypes.ONE_2_ONE_VAL);
				}
				factory.joinPropertyType(getRelationClazz(readMethod, clazz).getName());
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

	private void parseSequence(Method readMethod, FieldMeta.Factory fieldFactory) {
		if (readMethod.isAnnotationPresent(Sequence.class)) {
			Sequence seq = readMethod.getAnnotation(Sequence.class);
			fieldFactory.seqType(seq.generator());
			fieldFactory.seqKey(seq.key());
		}
	}

	private Class getRelationClazz(Method readMethod, Class objClazz) throws PlatformRuntimeException {
		try {
			Class returnType = readMethod.getReturnType();
			Class relatedObjClazz;
			if (org.springframework.util.ClassUtils.isAssignable(Collection.class, returnType)) {
				String propertyName = StringUtils.getPropertyNameByMethod(readMethod);
				relatedObjClazz = ClassUtils.getCollectionType(objClazz, propertyName);
			} else {
				relatedObjClazz = returnType;
			}
			return relatedObjClazz;
		} catch (Exception ex) {
			throw new PlatformRuntimeException(ex);
		}
	}

}
