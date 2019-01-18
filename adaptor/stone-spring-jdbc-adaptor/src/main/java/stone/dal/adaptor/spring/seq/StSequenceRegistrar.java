package stone.dal.adaptor.spring.seq;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import stone.dal.seq.autoconfigure.StSequenceConfig;

public class StSequenceRegistrar implements ImportBeanDefinitionRegistrar {

  private static final String BEAN = StSequenceConfig.class.getName();

  public static void register(BeanDefinitionRegistry registry, String storePath) {
    Assert.notNull(registry, "Registry must not be null");
    if (registry.containsBeanDefinition(BEAN)) {
      BeanDefinition beanDefinition = registry.getBeanDefinition(BEAN);
      ConstructorArgumentValues constructorArguments = beanDefinition
          .getConstructorArgumentValues();
      constructorArguments.addIndexedArgumentValue(0, storePath);
    } else {
      GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
      beanDefinition.setBeanClass(StSequenceConfig.class);
      beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, storePath);
      beanDefinition.setRole(BeanDefinition.ROLE_SUPPORT);
      registry.registerBeanDefinition(BEAN, beanDefinition);
    }
  }

  @Override
  public void registerBeanDefinitions(AnnotationMetadata meta, BeanDefinitionRegistry registry) {
    AnnotationAttributes attributes = AnnotationAttributes.fromMap(
        meta.getAnnotationAttributes(EnableSequence.class.getName()));
    String storePath = attributes.getString("storePath");
    register(registry, storePath);
  }
}
