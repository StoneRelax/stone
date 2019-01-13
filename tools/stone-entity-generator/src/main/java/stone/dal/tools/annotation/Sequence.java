package stone.dal.tools.annotation;

import java.lang.annotation.Inherited;

/**
 * Component: JBolt data dictionary
 * Description: Auto generated annotation
 * User: feng.xie
 * Date: Oct 4, 2010
 */
@Inherited
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Sequence {

    /**
     * Generator name
     *
     * @return Generator name
     */
    String generator() default "";

    /**
     * Number key
     *
     * @return Number key
     */
    String key() default "";

    boolean writeWhenNotEmpty() default false;
}
