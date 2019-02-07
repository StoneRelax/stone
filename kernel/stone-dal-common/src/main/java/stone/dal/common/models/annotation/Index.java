package stone.dal.common.models.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>Title: UniqueConstraint</p>
 * <p>Description: UniqueConstraint</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: Dr0ne Studio</p>
 *
 * @author smh
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Inherited
public @interface Index {

  /**
   * (Required) An array of the column names that make up the constraint.
   */
  String[] columnNames();

  String name();

  boolean unique() default false;
}