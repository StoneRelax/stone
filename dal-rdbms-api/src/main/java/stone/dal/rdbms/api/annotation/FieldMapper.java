package drone.platform.components.dal.rdbms.api.annotation;

import java.lang.annotation.Inherited;

@Inherited
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface FieldMapper {

	String mapper();

	String mappedBy() default "";

	String args() default "";
}
