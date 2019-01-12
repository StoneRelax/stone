package stone.dal.models.annotation;

import java.lang.annotation.Inherited;

/**
 * Component Name:
 * Description:
 *
 * @author feng.xie
 * @version $Revision: $
 */
@Inherited
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.METHOD,
    java.lang.annotation.ElementType.FIELD })
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface GroupByAllowed {
}
