/*
 * File: $RCSfile: ObjectPropertyDelegate.java,v $
 *
 * Copyright (c) 2008 Dr0ne.Dev Studio
 */
package stone.dal.tools.meta;

/**
 * Component scope: Utilities
 * Responsibilities: Property delegate
 *
 * @author feng.xie, Dr0ne.Dev Studio
 * @version $Revision: 1.9 $
 */
public interface ObjectPropertyDelegate {

    /**
     * Fetch property value
     *
     * @param propertyName Property name
     * @return Property value
     */
    Object fetchPropertyValue(String propertyName);

    /**
     * Register property
     *
     * @param propertyName Property name
     * @param property     Child models
     */
    void importPropertyValue(String propertyName, Object property);

    /**
     * Remove property
     *
     * @param propertyName Property name
     * @return Removed property value
     */
    Object removeProperty(String propertyName);

    /**
     * Fetch property type
     *
     * @param propertyName Property name
     * @return Type
     */
    Class fetchPropertyType(String propertyName);
}

