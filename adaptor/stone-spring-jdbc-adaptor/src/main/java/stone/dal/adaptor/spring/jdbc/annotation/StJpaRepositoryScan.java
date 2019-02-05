package stone.dal.adaptor.spring.jdbc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import stone.dal.adaptor.spring.jdbc.aop.StRepoBeanDefinitionJdbcRegistrar;
import stone.dal.adaptor.spring.jdbc.autoconfigure.SpringJdbcAdaptorAutoConfigure;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({ SpringJdbcAdaptorAutoConfigure.class, StRepoBeanDefinitionJdbcRegistrar.class })
public @interface StJpaRepositoryScan {

  String[] value() default {};

}

