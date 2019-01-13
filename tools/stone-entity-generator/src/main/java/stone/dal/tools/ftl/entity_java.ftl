package ${packageName};

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import stone.dal.tools.annotation.Sequence;
import stone.dal.tools.meta.SuperPojo;
import stone.dal.tools.annotation.FieldMapper;
import stone.dal.tools.annotation.Nosql;
import stone.dal.tools.annotation.UniqueIndex;
import stone.dal.tools.annotation.UniqueIndices;
import java.util.List;

@Entity
<#if gen.nosql(entity)>@Nosql</#if>${gen.extraHead(entity)}
@Table(name = "${entity.dbName}")
<#if gen.hasUniqueKeys(entity)>
@UniqueIndices(indices =
{<#list gen.uniqueIndices(entity) as idxName>
@UniqueIndex(name="${gen.dbIdxName(idxName)}",columnNames = {<#list gen.uniqueKeys(entity,idxName) as keyField>"${keyField}"<#if keyField_has_next>,</#if></#list>})
<#if idxName_has_next>,</#if>
</#list>})
</#if>
public class ${className} extends  ${gen.extClass(entity)} {

    <#list gen.fields2Add(entity) as dataField>
    private ${gen.getFieldType(entity,dataField)} ${dataField.name};
    </#list>
    <#list entity.relations as relation><#if gen.one2many(entity,relation)|| gen.many2many(entity,relation)>
    private List<${relation.joinPropertyType}> ${relation.joinProperty};<#else>
    private ${relation.joinPropertyType} ${relation.joinProperty};</#if>
    </#list>

    <#list gen.fields2Add(entity) as dataField>
    ${gen.getAnnotation(dataField)}
    public ${gen.getFieldType(entity, dataField)} get${gen.getMethodName(dataField.name)}(){
        return this.${dataField.name};
    }

    public void set${gen.getMethodName(dataField.name)}(${gen.getFieldType(entity,dataField)} ${dataField.name}){
        this.${dataField.name} = ${dataField.name};
    }
    </#list>

    <#list entity.relations as relation><#if gen.one2many(entity,relation)>
    @javax.persistence.OneToMany(cascade = {javax.persistence.CascadeType.ALL}<#if relation.mapperBy?exists>, mappedBy = "${relation.mapperBy}"</#if>)
    public java.util.List<${relation.joinPropertyType}> get${gen.getMethodName(relation.joinProperty)}(){
        return this.${relation.joinProperty};
    }

    public void set${gen.getMethodName(relation.joinProperty)}(java.util.List<${relation.joinPropertyType}> ${relation.joinProperty}){
        this.${relation.joinProperty} = ${relation.joinProperty};
    }
    <#elseif gen.many2many(entity,relation)>
    @javax.persistence.ManyToMany(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
    ${gen.many2manyAnnotation(entity, relation)}
    public java.util.List<${relation.joinPropertyType}> get${gen.getMethodName(relation.joinProperty)}(){
        return this.${relation.joinProperty};
    }

    public void set${gen.getMethodName(relation.joinProperty)}(java.util.List<${relation.joinPropertyType}> ${relation.joinProperty}){
        this.${relation.joinProperty} = ${relation.joinProperty};
    }
    <#else><#if gen.one2one(entity,relation)&&relation.joinColumnName?exists>
    @javax.persistence.OneToOne(cascade = {javax.persistence.CascadeType.REFRESH}, fetch = javax.persistence.FetchType.LAZY)<#else>
    @javax.persistence.ManyToOne(cascade = {javax.persistence.CascadeType.REFRESH}, fetch = javax.persistence.FetchType.LAZY)</#if>
    @javax.persistence.JoinColumn(name = "${relation.joinColumnName}", <#if relation.refColumn?exists>referencedColumnName = "${relation.refColumn}",</#if> nullable = ${relation.genNullableStr()}, updatable = ${relation.genUpdatableStr()})
    public ${relation.joinPropertyType} get${gen.getMethodName(relation.joinProperty)}(){
        return this.${relation.joinProperty};
    }

    public void set${gen.getMethodName(relation.joinProperty)}(${relation.joinPropertyType} ${relation.joinProperty}){
        this.${relation.joinProperty} = ${relation.joinProperty};
    }
    </#if>
    </#list>

    <#if gen.notExt(entity)>
    ${gen.localizationAccessor(entity)}
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getCanonicalClazz(getClass()) != getCanonicalClazz(o.getClass())) return false;

        ${className} obj = (${className}) o;
        <#list gen.getPks(entity) as pk>
        if (get${gen.getMethodName(pk)}() != null ? !get${gen.getMethodName(pk)}().equals(obj.get${gen.getMethodName(pk)}()) : obj.get${gen.getMethodName(pk)}() != null) return false;
        </#list>
        return true;
    }

    @Override
    public int hashCode() {
        <#list gen.getPks(entity) as pk>
        return get${gen.getMethodName(pk)}() != null ? get${gen.getMethodName(pk)}().hashCode() : 0;
        </#list>
    }
    </#if>

}