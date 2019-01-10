package stone.dal.common.api.meta;

/**
 * @author fengxie
 */
public abstract class FieldMeta {
	String seqKey;
	String seqType;
	String dbName;
	Boolean nullable = true;
	String name;
	Class type;
	Boolean pk;
	String widget;
	Integer maxLength;
	Integer scale;
	Integer precision;
	String mapper;
	String mappedBy;
	String widgetArgs;
	String order;
	Boolean groupByAllowed;
	Boolean notPersist;
	String comboId;
	String comboFilter;
	Boolean hideWhenQuery;
	String index;
	String tags;
	String codes;
	String codeTag;
	Boolean writeWhenNotEmpty; //used by seq only
	String dataAccess;
	Boolean file;
	String constraints;
	Boolean clob;
	Boolean i18n;
	Boolean insertable;
	Boolean updatable;

	public String getSeqKey() {
		return seqKey;
	}

	public String getSeqType() {
		return seqType;
	}

	public String getDbName() {
		return dbName;
	}

	public Boolean getNullable() {
		return nullable;
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

	public String getWidget() {
		return widget;
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

	public String getWidgetArgs() {
		return widgetArgs;
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

	public String getComboId() {
		return comboId;
	}

	public String getComboFilter() {
		return comboFilter;
	}

	public Boolean getHideWhenQuery() {
		return hideWhenQuery;
	}

	public String getIndex() {
		return index;
	}

	public String getTags() {
		return tags;
	}

	public String getCodes() {
		return codes;
	}

	public String getCodeTag() {
		return codeTag;
	}

	public Boolean getWriteWhenNotEmpty() {
		return writeWhenNotEmpty;
	}

	public String getDataAccess() {
		return dataAccess;
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
				", widget='" + widget + '\'' +
				", maxLength=" + maxLength +
				", scale=" + scale +
				", precision=" + precision +
				", mapper='" + mapper + '\'' +
				", mappedBy='" + mappedBy + '\'' +
				", widgetArgs='" + widgetArgs + '\'' +
				", order='" + order + '\'' +
				", groupByAllowed=" + groupByAllowed +
				", notPersist=" + notPersist +
				", comboId='" + comboId + '\'' +
				", comboFilter='" + comboFilter + '\'' +
				", hideWhenQuery=" + hideWhenQuery +
				", index='" + index + '\'' +
				", tags='" + tags + '\'' +
				", codes='" + codes + '\'' +
				", codeTag='" + codeTag + '\'' +
				", writeWhenNotEmpty=" + writeWhenNotEmpty +
				", dataAccess='" + dataAccess + '\'' +
				", file=" + file +
				", constraints='" + constraints + '\'' +
				", clob=" + clob +
				", i18n=" + i18n +
				", insertable=" + insertable +
				", updatable=" + updatable +
				'}';
	}

	public static class Factory {

		private FieldMeta meta = new FieldMeta() {
		};

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

		public Factory widget(String widget) {
			meta.widget = widget;
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

		public Factory widgetArgs(String widgetArgs) {
			meta.widgetArgs = widgetArgs;
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

		public Factory comboId(String comboId) {
			meta.comboId = comboId;
			return this;
		}

		public Factory comboFilter(String comboFilter) {
			meta.comboFilter = comboFilter;
			return this;
		}

		public Factory hideWhenQuery(Boolean hideWhenQuery) {
			meta.hideWhenQuery = hideWhenQuery;
			return this;
		}

		public Factory index(String index) {
			meta.index = index;
			return this;
		}

		public Factory tags(String tags) {
			meta.tags = tags;
			return this;
		}

		public Factory codes(String codes) {
			meta.codes = codes;
			return this;
		}

		public Factory codeTag(String codeTag) {
			meta.codeTag = codeTag;
			return this;
		}

		public Factory writeWhenNotEmpty(Boolean writeWhenNotEmpty) {
			meta.writeWhenNotEmpty = writeWhenNotEmpty;
			return this;
		}

		public Factory dataAccess(String dataAccess) {
			meta.dataAccess = dataAccess;
			return this;
		}

		public Factory file(Boolean file) {
			meta.file = file;
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

		public Factory insertable(Boolean insertable) {
			meta.insertable = insertable;
			return this;
		}

		public Factory updatable(Boolean updatable) {
			meta.updatable = updatable;
			return this;
		}

		public Factory i18n(Boolean i18n) {
			meta.i18n = i18n;
			return this;
		}

		public FieldMeta build() {
			return meta;
		}
	}
}
