package stone.dal.common.api.annotation;

import java.lang.annotation.Inherited;

/**
 * todo:description
 *
 * @author feng.xie
 * @version $Revision:
 */
@Inherited
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.METHOD,
    java.lang.annotation.ElementType.FIELD })
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface FileField {
}
