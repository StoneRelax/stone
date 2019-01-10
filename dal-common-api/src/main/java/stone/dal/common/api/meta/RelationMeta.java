package stone.dal.common.api.meta;

import stone.dal.common.api.RelationTypes;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * @author fengxie
 */
public abstract class RelationMeta {
	private RelationTypes relationType;
	private String joinProperty;
	private String joinPropertyType;
	private String mapperBy;
	private String joinTable;
	private Collection<JoinColumn> joinColumns = new HashSet<>();
	//only used by many 2 many
	private Collection<JoinColumn> inverseJoinColumns = new HashSet<>();
	private String fetchOrder;

	public String getFetchOrder() {
		return fetchOrder;
	}

	public RelationTypes getRelationType() {
		return relationType;
	}

	public String getJoinProperty() {
		return joinProperty;
	}

	public String getJoinPropertyType() {
		return joinPropertyType;
	}

	public String getMapperBy() {
		return mapperBy;
	}

	public String getJoinTable() {
		return joinTable;
	}

	public Collection<JoinColumn> getJoinColumns() {
		return Collections.unmodifiableCollection(joinColumns);
	}

	public Collection<JoinColumn> getInverseJoinColumns() {
		return Collections.unmodifiableCollection(inverseJoinColumns);
	}

	public static Factory factory() {
		return new Factory();
	}

	public static class Factory {

		private RelationMeta meta = new RelationMeta() {
		};

		public Factory relationType(RelationTypes relationType) {
			meta.relationType = relationType;
			return this;
		}

		public Factory joinProperty(String joinProperty) {
			meta.joinProperty = joinProperty;
			return this;
		}

		public Factory joinPropertyType(String joinPropertyType) {
			meta.joinPropertyType = joinPropertyType;
			return this;
		}

		public Factory mapperBy(String mapperBy) {
			meta.mapperBy = mapperBy;
			return this;
		}

		public Factory fetchOrder(String fetchOrder) {
			meta.fetchOrder = fetchOrder;
			return this;
		}

		public Factory joinTable(String joinTable) {
			meta.joinTable = joinTable;
			return this;
		}

		public Factory addJoinColumn(JoinColumn joinColumn) {
			meta.joinColumns.add(joinColumn);
			return this;
		}

		public Factory addInverseJoinColumn(JoinColumn joinColumnName) {
			meta.inverseJoinColumns.add(joinColumnName);
			return this;
		}

		public RelationMeta build() {
			return meta;
		}
	}
}
