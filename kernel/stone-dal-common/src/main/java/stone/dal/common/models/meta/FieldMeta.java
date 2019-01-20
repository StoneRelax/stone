package stone.dal.common.models.meta;

/**
 * @author fengxie
 */
public abstract class FieldMeta {
	protected String seqKey;

	protected String seqType;

	protected long seqStartNum;

	protected String dbName;

	protected Boolean nullable = true;

	protected Boolean insertable = true;

	protected String name;

	transient Class type;

	protected Boolean pk;

	protected Integer maxLength;

	protected Integer scale;

	protected Integer precision;

	protected String mapper;

	protected String mappedBy;

	protected String order;

	protected Boolean groupByAllowed;

	protected Boolean notPersist;

	protected String index;

	protected Boolean writeWhenNotEmpty; //used by seq only

	protected Boolean file;

	protected String constraints;

	protected Boolean clob;

	protected Boolean updatable;

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public String getSeqKey() {
		return seqKey;
	}

	public String getSeqType() {
		return seqType;
	}

	public long getSeqStartNum() {
		return seqStartNum;
	}

	public String getDbName() {
		return dbName;
	}

	public Boolean getNullable() {
		return nullable;
	}

	public Boolean getInsertable() {
		return insertable;
	}

	public Boolean getUpdatable() {
		return updatable;
	}

	public String getName() {
		return name;
	}

	public Class getType() {
		return type;
	}

	public Boolean getPk() {
		return pk;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public Integer getScale() {
		return scale;
	}

	public Integer getPrecision() {
		return precision;
	}

	public String getMapper() {
		return mapper;
	}

	public String getMappedBy() {
		return mappedBy;
	}

	public String getOrder() {
		return order;
	}

	public Boolean getGroupByAllowed() {
		return groupByAllowed;
	}

	public Boolean getNotPersist() {
		return notPersist;
	}

	public String getIndex() {
		return index;
	}

	public Boolean getWriteWhenNotEmpty() {
		return writeWhenNotEmpty;
	}

	public Boolean getFile() {
		return file;
	}

	public String getConstraints() {
		return constraints;
	}

	public Boolean getClob() {
		return clob;
	}

	public static Factory factory() {
		return new Factory();
	}

	@Override
	public String toString() {
		return "FieldMeta{" +
				"dbName='" + dbName + '\'' +
				",name='" + name + '\'' +
				", type=" + type +
				",seqKey='" + seqKey + '\'' +
				", seqType='" + seqType + '\'' +
				", nullable=" + nullable +
				", pk=" + pk +
				", maxLength=" + maxLength +
				", scale=" + scale +
				", precision=" + precision +
				", mapper='" + mapper + '\'' +
				", mappedBy='" + mappedBy + '\'' +
				", order='" + order + '\'' +
				", groupByAllowed=" + groupByAllowed +
				", index='" + index + '\'' +
				", writeWhenNotEmpty=" + writeWhenNotEmpty +
				", file=" + file +
				", constraints='" + constraints + '\'' +
				", clob=" + clob +
				", updatable=" + updatable +
				'}';
	}

	public static class Factory {

		private FieldMeta meta = new FieldMeta() {
		};

		public Factory insertable(Boolean insertable) {
			meta.insertable = insertable;
			return this;
		}

		public Factory seqKey(String seqKey) {
			meta.seqKey = seqKey;
			return this;
		}

		public Factory seqType(String seqType) {
			meta.seqType = seqType;
			return this;
		}

		public Factory dbName(String dbName) {
			meta.dbName = dbName;
			return this;
		}

		public Factory nullable(Boolean nullable) {
			meta.nullable = nullable;
			return this;
		}

		public Factory name(String name) {
			meta.name = name;
			return this;
		}

		public Factory type(Class type) {
			meta.type = type;
			return this;
		}

		public Factory pk(Boolean pk) {
			meta.pk = pk;
			return this;
		}

		public Factory maxLength(Integer maxLength) {
			meta.maxLength = maxLength;
			return this;
		}

		public Factory scale(Integer scale) {
			meta.scale = scale;
			return this;
		}

		public Factory precision(Integer precision) {
			meta.precision = precision;
			return this;
		}

		public Factory mapper(String mapper) {
			meta.mapper = mapper;
			return this;
		}

		public Factory mappedBy(String mappedBy) {
			meta.mappedBy = mappedBy;
			return this;
		}

		public Factory order(String order) {
			meta.order = order;
			return this;
		}

		public Factory groupByAllowed(Boolean groupByAllowed) {
			meta.groupByAllowed = groupByAllowed;
			return this;
		}

		public Factory notPersist(Boolean notPersist) {
			meta.notPersist = notPersist;
			return this;
		}

		public Factory index(String index) {
			meta.index = index;
			return this;
		}

		public Factory writeWhenNotEmpty(Boolean writeWhenNotEmpty) {
			meta.writeWhenNotEmpty = writeWhenNotEmpty;
			return this;
		}

		public Factory file(Boolean file) {
			meta.file = file;
			return this;
		}

		public Factory defaultStartSeq(long defaultStartSeq) {
			meta.seqStartNum = defaultStartSeq;
			return this;
		}

		public Factory constraints(String constraints) {
			meta.constraints = constraints;
			return this;
		}

		public Factory clob(Boolean clob) {
			meta.clob = clob;
			return this;
		}

		public Factory updatable(Boolean updatable) {
			meta.updatable = updatable;
			return this;
		}

		public FieldMeta build() {
			return meta;
		}
	}
}
