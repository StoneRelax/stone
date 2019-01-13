package stone.dal.tools.meta;


import stone.dal.models.meta.EntityMeta;
import stone.dal.models.meta.FieldMeta;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;

/**
 * Component Name:
 * Description:
 *
 * @author feng.xie
 * @version $Revision: 1.1 $
 */
public interface DataDictionary {

    /**
     * Register entities
     *
     * @param entities All entities object.
     */
    void register(List<EntityMeta> entities);

    /**
     * Register domain by stream
     *
     * @param is Stream
     */
    void registerByStream(InputStream is);

    /**
     * Clear
     */
    void clear();

    /**
     * Return orm object meta instance by specified object name
     *
     * @param objectName object name
     * @return object meta instance
     */
    EntityMeta getEntityMeta(String objectName);

    /**
     * Delete entity by a given name
     *
     * @param entityName Entity name
     */
    void delEntity(String entityName);

    /**
     * Read children properties' name of entity
     *
     * @param entity Entity
     * @return Properties
     */
    HashSet<String> children(String entity);

    /**
     * Return child entity name
     *
     * @param clazz    Domain class
     * @param property Property name
     * @return Child entity name
     */
    String getChildEntityName(Class clazz, String property);

    /**
     * Return child property entity
     *
     * @param entityName       Entity name
     * @param propertySelector Property selector
     * @return Data entity meta
     */
    EntityMeta relatedEntity(String entityName, String propertySelector);

    /**
     * Return orm object meta instance by specified object name
     *
     * @param objectName object name
     * @return object meta instance
     */
    EntityMeta getEntityMetaByDbTable(String objectName);

    /**
     * Return entity meta by specified mapping class
     *
     * @param clazz Map class
     * @return Object meta instance
     */
    EntityMeta readEntityByClazz(Class clazz);

    /**
     * Return data field meta
     *
     * @param entity    Data entity meta
     * @param fieldName Field name
     * @return Field instance
     */
    FieldMeta dataField(EntityMeta entity, String fieldName);

    <T> T createNodeObj(String domain) throws Exception;

    /**
     * Find data access field
     *
     * @param entity     Entity meta
     * @param accessType Access Type
     * @return Field
     */
    FieldMeta findDataAccessField(EntityMeta entity, String accessType);

    /**
     * Return data field meta by db field
     *
     * @param entity    Data entity meta
     * @param fieldName Field name
     * @return Field instance
     */
    FieldMeta readFieldByDbField(EntityMeta entity, String fieldName);

    /**
     * Return property type for specified object name and property name
     *
     * @param objectName   object name
     * @param propertyName property name
     * @return property type
     */
    String getFieldType(String objectName, String propertyName);

    /**
     * return class of specified object name and property name
     *
     * @param objectName   object name
     * @param propertyName property name
     * @return property class
     */
    Class getFieldClazz(String objectName, String propertyName);

    /**
     * Return registered object names
     *
     * @return Managed entity names.
     */
    String[] getAllEntities();

    /**
     * Return entity names of which have file field
     *
     * @return Entity names
     */
    String[] includeFileEntities();

    /**
     * Has File file
     * @param name Name
     * @return True indicates it has file fields
     */
    boolean hasFileField(String name);

	/**
	 * Return if entity has clob field
     * @param name Name
     * @return True indicates it has
     */
    boolean hasClobField(String name);

    /**
     * Get required mapping field
     *
     * @param entityMeta Entity meta
     * @return Fields
     */
    List<FieldMeta> getRequireMappingField(EntityMeta entityMeta);

    /**
     * Read constraints
     *
     * @param entity Entity name
     * @return Constraints list
     */
    HashSet<EntityConstraints> readConstraints(String entity);

}
