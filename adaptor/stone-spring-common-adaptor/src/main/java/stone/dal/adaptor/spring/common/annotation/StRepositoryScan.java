package stone.dal.adaptor.spring.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import stone.dal.adaptor.spring.common.aop.StRepoBeanDefinitionRegistrar;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(StRepoBeanDefinitionRegistrar.class)
public @interface StRepositoryScan {

  String[] value() default {};

}

