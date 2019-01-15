package stone.dal.jdbc.spring.adaptor.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import stone.dal.jdbc.spring.adaptor.impl.StBeanDefinitionRegistrar;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(StBeanDefinitionRegistrar.class)
public @interface StRepositoryScan {
  String[] value() default {};
}
