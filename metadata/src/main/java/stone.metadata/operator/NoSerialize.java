package stone.dal.metadata.operator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;

/**
 * @author fengxie
 */
@Inherited
@java.lang.annotation.Target({ElementType.METHOD})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface NoSerialize {
}