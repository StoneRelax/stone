/*
 * File: $RCSfile: DataFieldMeta.java,v $
 *
 * Copyright (c) 2008 Dr0ne.Dev Studio
 */
package stone.dal.tools.meta;

import stone.dal.models.meta.FieldMeta;

/**
 * Component scope: JBolt Data Dictionary
 * Responsibilities: Data field meta
 *
 * @author feng.xie, Dr0ne.Dev Studio
 * @version $Revision: 1.2 $
 */
public class DataFieldMeta extends FieldMeta {

    private String seqKey;
    private String seqType;
    private String defaultStartSeq;
    private String dbName;
    private String typeName;
    private Boolean nullable = true;
    private String name;
    private String label;
    private Boolean pk;
    private String format;
    private String wname;
    private Integer maxlength;
    private Integer scale;
    private Integer precision;
    private Boolean i18n;
    private String mapper;
    private String mappedBy;
    private String mapperArgs;
    private String wargs;
    private Boolean orderBy;
    private String order;
    private Boolean groupBy;
    private Boolean nodb;
    private String comboid;
    private String filter;
    private String alias;
    private Boolean noQuery;
    private String func;
    private String unique;
    private String index;
    private String entity;
    private String capital;
    private String encrypt;
    private String remarks;
    private String tags;
    private String codes;
    private String codeTags;
    private Boolean writeWhenNotEmpty; //used by seq only
    private String dataAccess;
    private String md5;
    private Boolean fileField;
    private String constraints;
    private Boolean addOn; //only serves when entity is extension of builit-in entity
    private Boolean clob;

    public Boolean getClob() {
        return clob;
    }

    public void setClob(Boolean clob) {
        this.clob = clob;
    }

    public Boolean getAddOn() {
        return addOn;
    }

    public void setAddOn(Boolean addOn) {
        this.addOn = addOn;
    }

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }

    private transient String dbTableName;
    private String entitySelector;

    public Boolean getFileField() {
        return fileField;
    }

    public void setFileField(Boolean fileField) {
        this.fileField = fileField;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getDataAccess() {
        return dataAccess;
    }

    public void setDataAccess(String dataAccess) {
        this.dataAccess = dataAccess;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(String encrypt) {
        this.encrypt = encrypt;
    }

    public Boolean getWriteWhenNotEmpty() {
        return writeWhenNotEmpty;
    }

    public void setWriteWhenNotEmpty(Boolean writeWhenNotEmpty) {
        this.writeWhenNotEmpty = writeWhenNotEmpty;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getWargs() {
        return wargs;
    }

    public void setWargs(String wargs) {
        this.wargs = wargs;
    }

    public String getSeqKey() {
        return seqKey;
    }

    public void setSeqKey(String seqKey) {
        this.seqKey = seqKey;
    }

    public String getSeqType() {
        return seqType;
    }

    public void setSeqType(String seqType) {
        this.seqType = seqType;
    }

    public String getDefaultStartSeq() {
        return defaultStartSeq;
    }

    public void setDefaultStartSeq(String defaultStartSeq) {
        this.defaultStartSeq = defaultStartSeq;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
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

    public Boolean getPk() {
        return pk;
    }

    public void setPk(Boolean pk) {
        this.pk = pk;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getWname() {
        return wname;
    }

    public void setWname(String wname) {
        this.wname = wname;
    }

    public Integer getMaxlength() {
        return maxlength;
    }

    public void setMaxlength(Integer maxlength) {
        this.maxlength = maxlength;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Boolean getI18n() {
        return i18n;
    }

    public void setI18n(Boolean i18n) {
        this.i18n = i18n;
    }

    public String getMapper() {
        return mapper;
    }

    public void setMapper(String mapper) {
        this.mapper = mapper;
    }

    public String getMappedBy() {
        return mappedBy;
    }

    public void setMappedBy(String mappedBy) {
        this.mappedBy = mappedBy;
    }

    public String getMapperArgs() {
        return mapperArgs;
    }

    public void setMapperArgs(String mapperArgs) {
        this.mapperArgs = mapperArgs;
    }

    public Boolean getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Boolean orderBy) {
        this.orderBy = orderBy;
    }

    public Boolean getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(Boolean groupBy) {
        this.groupBy = groupBy;
    }

    public Boolean getNodb() {
        return nodb;
    }

    public void setNodb(Boolean nodb) {
        this.nodb = nodb;
    }

    public String getComboid() {
        return comboid;
    }

    public void setComboid(String comboid) {
        this.comboid = comboid;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Boolean getNoQuery() {
        return noQuery;
    }

    public void setNoQuery(Boolean noQuery) {
        this.noQuery = noQuery;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getDbTableName() {
        return dbTableName;
    }

    public void setDbTableName(String dbTableName) {
        this.dbTableName = dbTableName;
    }

    public String getEntitySelector() {
        return entitySelector;
    }

    public void setEntitySelector(String entitySelector) {
        this.entitySelector = entitySelector;
    }

    public String getCodes() {
        return codes;
    }

    public void setCodes(String codes) {
        this.codes = codes;
    }

    public String getCodeTags() {
        return codeTags;
    }

    public void setCodeTags(String codeTags) {
        this.codeTags = codeTags;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String type) {
        this.typeName = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataFieldMeta that = (DataFieldMeta) o;

        if (!entity.equals(that.entity)) return false;
        if (entitySelector != null ? !entitySelector.equals(that.entitySelector) : that.entitySelector != null)
            return false;
        if (func != null ? !func.equals(that.func) : that.func != null) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + entity.hashCode();
        result = 31 * result + (entitySelector != null ? entitySelector.hashCode() : 0);
        result = 31 * result + (func != null ? func.hashCode() : 0);
        return result;
    }
}
/**
 * History:
 *
 * $Log: DataFieldMeta.java,v $
 * Revision 1.2  2010/03/15 09:59:53  fxie
 * no message
 *
 * Revision 1.1  2010/02/01 08:53:16  fxie
 * no message
 *
 * Revision 1.4  2009/10/21 14:06:34  fxie
 * no message
 *
 * Revision 1.3  2009/08/22 13:59:01  fxie
 * no message
 *
 * Revision 1.2  2009/05/24 17:36:45  fxie
 * *** empty log message ***
 *
 * Revision 1.1  2009/03/05 11:23:59  fxie
 * no message
 *
 */


