package stone.dal.models.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author fengxie
 */
public abstract class EntityMeta {
	String tableName;
	transient Class clazz;
	Collection<FieldMeta> fields;
	Collection<RelationMeta> relations;
	Collection<UniqueIndexMeta> uniqueIndices;

	public String getTableName() {
		return tableName;
	}

	public Class getClazz() {
		return clazz;
	}

	public Collection<FieldMeta> getFields() {
		return fields;
	}

	public Collection<RelationMeta> getRelations() {
		return relations;
	}

	public Collection<UniqueIndexMeta> getUniqueIndices() {
		return uniqueIndices;
	}

	public static Factory factory() {
		return new Factory();
	}

	public static class Factory {

		private List<FieldMeta> fields = new ArrayList<>();
		private List<RelationMeta> relations = new ArrayList<>();
		private List<UniqueIndexMeta> uniqueIndices = new ArrayList<>();

		private EntityMeta meta = new EntityMeta() {
		};

		public Factory tableName(String tableName) {
			meta.tableName = tableName;
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

		public Factory addUniqueMeta(UniqueIndexMeta uniqueIndexMeta) {
			uniqueIndices.add(uniqueIndexMeta);
			return this;
		}

		public EntityMeta build() {
			meta.fields = Collections.unmodifiableCollection(fields);
			meta.relations = Collections.unmodifiableCollection(relations);
			meta.uniqueIndices = Collections.unmodifiableCollection(uniqueIndices);
			return meta;
		}
	}
}
