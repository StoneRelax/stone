package stone.dal.common.models.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>Title: Nosql</p>
 * <p>Description: Nosql</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: Dr0ne Studio</p>
 *
 * @author smh
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Nosql {
}
