package stone.dal.jdbc.spring.adaptor.aop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import stone.dal.jdbc.spring.adaptor.annotation.StRepositoryScan;
import stone.dal.kernel.utils.CGLibUtils;
import stone.dal.kernel.utils.KernelRuntimeException;
import stone.dal.kernel.utils.LogUtils;

import static org.springframework.core.io.support.ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;

public class StBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

  private static Logger logger = LoggerFactory.getLogger(StBeanDefinitionRegistrar.class);

  private List<TypeFilter> interfaceFilter;

  private List<TypeFilter> abstractClassFilter;

  private List<TypeFilter> excludeFilter;

  private StJpaRepositoryMethodFilter jpaRepositoryMethodFilter;

  private StJpaRepositoryMethodInterceptor jpaRepositoryMethodInterceptor;

  public StBeanDefinitionRegistrar() {
    jpaRepositoryMethodFilter = new StJpaRepositoryMethodFilter();
    jpaRepositoryMethodInterceptor = new StJpaRepositoryMethodInterceptor();
  }

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    AnnotationAttributes annAttr = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(
        StRepositoryScan.class.getName()));
    String[] basePackages = annAttr.getStringArray("value");
    List<Class<?>> interfaceDoRepositories = scanPackages(basePackages, interfaceFilter, excludeFilter);
    for (Class<?> repoClass : interfaceDoRepositories) {
      Class enhancedClass = build(repoClass);
      RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(enhancedClass);
      registry.registerBeanDefinition(enhancedClass.getName(), rootBeanDefinition);
    }
  }

  private List<Class<?>> scanPackages(String[] basePackages, List<TypeFilter> includeFilters,
      List<TypeFilter> excludeFilters) {
    List<Class<?>> candidates = new ArrayList<Class<?>>();
    for (String pkg : basePackages) {
      try {
        candidates.addAll(findCandidateClasses(pkg, includeFilters, excludeFilters));
      } catch (IOException e) {
        logger.error("Exception when scanning DO repositories", pkg);
      }
    }
    return candidates;
  }

  private List<Class<?>> findCandidateClasses(String basePackage, List<TypeFilter> includeFilters,
      List<TypeFilter> excludeFilters) throws IOException {
    if (logger.isDebugEnabled()) {
      logger.debug("Scanning DO repositories in package : " + basePackage);
    }
    List<Class<?>> candidates = new ArrayList<Class<?>>();
    String packageSearchPath = CLASSPATH_ALL_URL_PREFIX + ClassUtils
        .convertClassNameToResourcePath(basePackage) + "/*";
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    MetadataReaderFactory readerFactory = new SimpleMetadataReaderFactory(resourceLoader);
    Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
        .getResources(packageSearchPath);
    for (Resource resource : resources) {
      MetadataReader reader = readerFactory.getMetadataReader(resource);
      if (isCandidateResource(reader, readerFactory, includeFilters, excludeFilters)) {
        try {
          Class<?> candidateClass = Class.forName(reader.getClassMetadata().getClassName());
          if (candidateClass != null) {
            candidates.add(candidateClass);
            logger.debug("DO repository scanned : " + candidateClass.getName());
          }
        } catch (Exception e) {
          logger.error("Error when scanning DO repository " + e);
        }

      }
    }
    return candidates;
  }

  private boolean isCandidateResource(MetadataReader reader, MetadataReaderFactory readerFactory,
      List<TypeFilter> includeFilter, List<TypeFilter> excludeFilter) {
    //todo impl
    if (reader.getClassMetadata().isInterface()) {
      return true;
    }
    return false;
  }

  Class build(Class clazz) {
    Class repoClazz = null;
    try {
      if (clazz.isInterface()) {
        repoClazz = CGLibUtils.buildProxyClass(clazz, jpaRepositoryMethodInterceptor, jpaRepositoryMethodFilter);
      } else {
        repoClazz = clazz.getSuperclass();
      }
    } catch (Exception e) {
      LogUtils.error(logger, e);
      throw new KernelRuntimeException(e);
    }
    return repoClazz;
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