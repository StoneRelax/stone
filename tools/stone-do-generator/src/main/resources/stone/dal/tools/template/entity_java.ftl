package ${packageName};

@javax.persistence.Entity<#if gen.nosql(entity)>@stone.dal.common.models.annotation.Nosql</#if>
@javax.persistence.Table(name = "${entity.tableName}")
<#if gen.hasIndex(entity)>@stone.dal.common.models.annotation.Indicies(indices = {<#list gen.indicies(entity) as idxName>
@stone.dal.common.models.annotation.Index(name="${gen.dbIdxName(entity, idxName)}",unique=${gen.isUnique(entity, idxName)}, columnNames = {<#list gen.getIndexColumns(entity,idxName) as keyField>"${keyField}"<#if keyField_has_next>,</#if></#list>})<#if idxName_has_next>,</#if></#list>})</#if><#if gen.hasListenerIntf(entity)>
@javax.persistence.EntityListeners({
<#list entity.entityListeners as entityListener>
    ${entityListener.className}.class<#if entityListener_has_next>,</#if>
</#list>})</#if>
public class ${className} extends stone.dal.common.models.data.BaseDo <#if gen.hasListenerIntf(entity)>implements <#list entity.entityListeners as entityListener>${entityListener.interfaceName}<#if entityListener_has_next>,</#if></#list></#if> {

    <#list entity.getRawFields() as dataField>
    private ${gen.getFieldType(entity,dataField)} ${dataField.name};
    </#list>

    <#list entity.rawRelations as relation><#if gen.one2many(relation)|| gen.many2many(relation)>
    private java.util.List<${relation.getJoinPropertyTypeName()}> ${relation.joinProperty};<#else>
    private ${relation.getJoinPropertyTypeName()} ${relation.joinProperty};</#if>
    </#list>

    <#list entity.getRawFields() as dataField>
    ${gen.getAnnotation(dataField)}
    public ${gen.getFieldType(entity, dataField)} get${gen.getMethodName(dataField.name)}(){
        return this.${dataField.name};
    }

    public void set${gen.getMethodName(dataField.name)}(${gen.getFieldType(entity,dataField)} ${dataField.name}){
        this.${dataField.name} = ${dataField.name};
    }
    </#list>

    <#list entity.rawRelations as relation><#if gen.one2many(relation)>
    @javax.persistence.OneToMany(cascade = {javax.persistence.CascadeType.ALL}<#if relation.mappedBy?exists>, mappedBy = "${relation.mappedBy}"</#if>)
    public java.util.List<${relation.getJoinPropertyTypeName()}> get${gen.getMethodName(relation.joinProperty)}(){
        return this.${relation.joinProperty};
    }

    public void set${gen.getMethodName(relation.joinProperty)}(java.util.List<${relation.getJoinPropertyTypeName()}> ${relation.joinProperty}){
        this.${relation.joinProperty} = ${relation.joinProperty};
    }
    <#elseif gen.many2many(relation)>
    @javax.persistence.ManyToMany(cascade = {javax.persistence.CascadeType.REFRESH}, fetch = javax.persistence.FetchType.LAZY)
    ${gen.many2manyAnnotation(entity, relation, entityDict)}
    public java.util.List<${relation.joinPropertyTypeName}> get${gen.getMethodName(relation.joinProperty)}(){
        return this.${relation.joinProperty};
    }

    public void set${gen.getMethodName(relation.joinProperty)}(java.util.List<${relation.joinPropertyTypeName}> ${relation.joinProperty}){
        this.${relation.joinProperty} = ${relation.joinProperty};
    }
    <#else><#if gen.one2one(relation)&&relation.joinColumnName?exists>
    @javax.persistence.OneToOne(cascade = {javax.persistence.CascadeType.REFRESH}, fetch = javax.persistence.FetchType.LAZY)<#else>
    @javax.persistence.ManyToOne(cascade = {javax.persistence.CascadeType.REFRESH}, fetch = javax.persistence.FetchType.LAZY)</#if>
    @javax.persistence.JoinColumn(name = "${relation.joinColumnName}", <#if relation.refColumn?exists>referencedColumnName = "${relation.refColumn}",</#if> nullable = ${relation.nullable()}, updatable = ${relation.updatable()})
    public ${relation.joinPropertyTypeName} get${gen.getMethodName(relation.joinProperty)}(){
        return this.${relation.joinProperty};
    }

    public void set${gen.getMethodName(relation.joinProperty)}(${relation.joinPropertyTypeName} ${relation.joinProperty}){
        this.${relation.joinProperty} = ${relation.joinProperty};
    }
    </#if>

    </#list>

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

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

}