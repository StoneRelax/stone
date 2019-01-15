package stone.dal.jdbc.spring.adaptor.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.TypeFilter;
import stone.dal.jdbc.spring.adaptor.annotation.StRepositoryScan;

public class StBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

  private static Logger logger = LoggerFactory.getLogger(StBeanDefinitionRegistrar.class);

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    AnnotationAttributes annAttr = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(
        StRepositoryScan.class.getName()));
    String[] basePackages = annAttr.getStringArray("value");

  }

  private List<Class<?>> scanPackages(String[] basePackages, List<TypeFilter> includeFilters,
      List<TypeFilter> excludeFilters) {
    List<Class<?>> candidates = new ArrayList<Class<?>>();
    for (String pkg : basePackages) {
      try {
        candidates.addAll(findCandidateClasses(pkg, includeFilters, excludeFilters));
      } catch (IOException e) {
        logger.error("扫描指定HSF基础包[{}]时出现异常", pkg);
      }
    }
    return candidates;
  }

  private List<Class<?>> findCandidateClasses(String basePackage, List<TypeFilter> includeFilters,
      List<TypeFilter> excludeFilters) throws IOException {
//    if (LOGGER.isDebugEnabled()) {
//      LOGGER.debug("开始扫描指定包{}下的所有类" + basePackage);
//    }
//    List<Class<?>> candidates = new ArrayList<Class<?>>();
//    String packageSearchPath = CLASSPATH_ALL_URL_PREFIX + replaceDotByDelimiter(basePackage) + '/' + RESOURCE_PATTERN;
//    ResourceLoader resourceLoader = new DefaultResourceLoader();
//    MetadataReaderFactory readerFactory = new SimpleMetadataReaderFactory(resourceLoader);
//    Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(packageSearchPath);
//    for (Resource resource : resources) {
//      MetadataReader reader = readerFactory.getMetadataReader(resource);
//      if (isCandidateResource(reader, readerFactory, includeFilters, excludeFilters)) {
//        Class<?> candidateClass = transform(reader.getClassMetadata().getClassName());
//        if (candidateClass != null) {
//          candidates.add(candidateClass);
//          LOGGER.debug("扫描到符合要求HSF基础类:{}" + candidateClass.getName());
//        }
//      }
//    }
//    return candidates;
    return null;
  }

//  private void registerBeanDefinitions(List<Class<?>> internalClasses, BeanDefinitionRegistry registry) {
//    for (Class<?> clazz : internalClasses) {
//      if (HSF_UNDERLYING_MAPPING.values().contains(clazz)) {
//        LOGGER.debug("重复扫描{}类,忽略重复注册", clazz.getName());
//        continue;
//      }
//      String beanName = generateHsfBeanName(clazz);
//      RootBeanDefinition rbd = new RootBeanDefinition(HSFSpringProviderBean.class);
//      registry.registerBeanDefinition(beanName, rbd);
//      if (registerSpringBean(clazz)) {
//        LOGGER.debug("注册HSF基础[{}]Bean", clazz.getName());
//        registry.registerBeanDefinition(ClassUtils.getShortNameAsProperty(clazz), new RootBeanDefinition(clazz));
//      }
//      HSF_UNDERLYING_MAPPING.put(beanName, clazz);
//    }
//  }
//
//  /**
//   * 注册HSF后处理器
//   *
//   * @param registry
//   */
//  private void registerHsfBeanPostProcessor(BeanDefinitionRegistry registry) {
//    String beanName = ClassUtils.getShortNameAsProperty(HsfBeanPostProcessor.class);
//    if (!registry.containsBeanDefinition(beanName)) {
//      registry.registerBeanDefinition(beanName, new RootBeanDefinition(HsfBeanPostProcessor.class));
//    }
//  }
}
