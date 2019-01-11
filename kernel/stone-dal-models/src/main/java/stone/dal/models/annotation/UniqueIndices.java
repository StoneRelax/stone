package stone.dal.models.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>Title: UniqueIndices</p>
 * <p>Description: UniqueIndices</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: Dr0ne Studio</p>
 *
 * @author smh
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Inherited
public @interface UniqueIndices {
  UniqueIndex[] indices();
}
