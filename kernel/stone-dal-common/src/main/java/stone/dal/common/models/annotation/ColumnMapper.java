package stone.dal.common.models.annotation;

import java.lang.annotation.Inherited;

/**
 * Component:Data Dictionary
 * Description:Description mappping
 * User: feng.xie
 * Date: 30-Dec-2010
 */
@Inherited
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.METHOD,
    java.lang.annotation.ElementType.FIELD })
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface ColumnMapper {

  Class mapper();

  String associateColumn() default "";

  String args() default "";
}
