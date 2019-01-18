package stone.dal.adaptor.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import stone.dal.adaptor.spring.jdbc.aop.StJpaRepoBeanDefinitionRegistrar;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(StJpaRepoBeanDefinitionRegistrar.class)
public @interface StRepositoryScan {

  String[] value() default {};
}
