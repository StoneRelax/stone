package stone.dal.common.models.annotation;

import java.lang.annotation.Inherited;

/**
 * @author fengxie
 */
@Inherited
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.METHOD,
    java.lang.annotation.ElementType.FIELD })
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Clob {
}
