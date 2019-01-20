package stone.dal.adaptor.spring.common.aop;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.MethodInterceptor;
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
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import stone.dal.adaptor.spring.common.annotation.StRepositoryScan;
import stone.dal.adaptor.spring.common.utils.DalAopUtils;
import stone.dal.kernel.utils.CGLibUtils;
import stone.dal.kernel.utils.KernelRuntimeException;
import stone.dal.kernel.utils.LogUtils;

import static org.springframework.core.io.support.ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;

public class StRepoBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

  private static Logger logger = LoggerFactory.getLogger(StRepoBeanDefinitionRegistrar.class);

  private static final TypeFilter interfaceFilter = (metadataReader, metadataReaderFactory) -> metadataReader
      .getClassMetadata().isInterface();

  //todo: should consider not abstract case
  private static final TypeFilter abstractClassFilter = (metadataReader, metadataReaderFactory) ->
      metadataReader
          .getClassMetadata().isAbstract() && !metadataReader
          .getClassMetadata().isInterface();

  private TypeFilter excludeFilter = (metadataReader, metadataReaderFactory) -> false;

  private List<AopConf> aopConfList = new ArrayList<>();

  public StRepoBeanDefinitionRegistrar() {
    try {
      Enumeration<URL> urls = StRepoBeanDefinitionRegistrar.class.getClassLoader()
          .getResources("META-INF/dal-support.properties");
      while (urls.hasMoreElements()) {
        URL url = urls.nextElement();
        Properties properties = new Properties();
        InputStream is = url.openStream();
        properties.load(is);
        aopConfList.add(new AopConf(properties));
      }
    } catch (IOException e) {
      LogUtils.error(logger, e);
      throw new KernelRuntimeException(e);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    AnnotationAttributes annAttr = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(
        StRepositoryScan.class.getName()));
    String[] basePackages = annAttr.getStringArray("value");
    List<Class<?>> intfRepoClasses = scanPackages(basePackages, interfaceFilter, excludeFilter);
    Set<Class> filterOutIntfClasses = new HashSet<>();
    List<Class<?>> abstractRepoClazz = scanPackages(basePackages, abstractClassFilter, excludeFilter);
    abstractRepoClazz.forEach(repoClazz -> {
      for (Class intf : intfRepoClasses) {
        if (intf.isAssignableFrom(repoClazz)) {
          filterOutIntfClasses.add(intf);
        }
      }
    });
    intfRepoClasses.removeAll(filterOutIntfClasses);
    LinkedList<Class<?>> allRepoClasses = new LinkedList<>();
    allRepoClasses.addAll(intfRepoClasses);
    allRepoClasses.addAll(abstractRepoClazz);
    for (Class<?> repositoryClazz : allRepoClasses) {
      for (AopConf conf : aopConfList) {
        if (conf.intfClazz.isAssignableFrom(repositoryClazz)) {
          Collection<Method> methods = conf.filterParsableMethods(repositoryClazz.getMethods());
          methods.forEach(method -> {
            StRepoMethodPartRegistry.getInstance().registerMethod(method,
                DalAopUtils.getDoClass(repositoryClazz), conf.repoQueryByMethodNameClazz);
          });
          Class enhancedClass = build(repositoryClazz, conf.methodFilter, conf.methodInterceptor);
          RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(enhancedClass);
          registry.registerBeanDefinition(enhancedClass.getName(), rootBeanDefinition);
          break;
        }
      }
    }
  }

  private List<Class<?>> scanPackages(String[] basePackages, TypeFilter includeFilter,
      TypeFilter excludeFilter) {
    List<Class<?>> candidates = new ArrayList<>();
    for (String pkg : basePackages) {
      try {
        candidates.addAll(findCandidateClasses(pkg, includeFilter, excludeFilter));
      } catch (Exception e) {
        logger.error("Exception when scanning DO repositories", pkg);
      }
    }
    return candidates;
  }

  private List<Class<?>> findCandidateClasses(String basePackage, TypeFilter includeFilter,
      TypeFilter excludeFilter) throws Exception {
    if (logger.isDebugEnabled()) {
      logger.debug("Scanning DO repositories in package : " + basePackage);
    }
    List<Class<?>> candidates = new ArrayList<>();
    String packageSearchPath = CLASSPATH_ALL_URL_PREFIX + ClassUtils
        .convertClassNameToResourcePath(basePackage) + "/*";
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    MetadataReaderFactory readerFactory = new SimpleMetadataReaderFactory(resourceLoader);
    Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
        .getResources(packageSearchPath);
    for (Resource resource : resources) {
      MetadataReader reader = readerFactory.getMetadataReader(resource);
      if (isCandidateResource(reader, readerFactory, includeFilter)) {
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
      TypeFilter includeFilter) throws Exception {
    return includeFilter.match(reader, readerFactory);
  }

  static class AopConf {
    Class intfClazz;

    MethodInterceptor methodInterceptor;

    CallbackFilter methodFilter;

    Class repoQueryByMethodNameClazz;

    Set<Method> methodsOfSuperIntf = new HashSet<>();

    AopConf(Properties properties) {
      String interfaceClass = properties.getProperty("repo.interface.class");
      String methodInterceptorName = properties.getProperty("aop.methodInterceptor.class");
      String methodFilterName = properties.getProperty("aop.methodFilter.class");
      String repoQueryByMethodNameClass = properties.getProperty("aop.queryOnMethod.class");
      Assert.notNull(interfaceClass, "repo.interface.class can't be not null");
      Assert.notNull(methodFilterName, "aop.methodInterceptor can't be not null");
      Assert.notNull(methodInterceptorName, "aop.methodFilter can't be not null");
      Assert.notNull(repoQueryByMethodNameClass, "aop.queryOnMethod.class can't be not null");
      try {
        intfClazz = Class.forName(interfaceClass);
        methodFilter = (CallbackFilter) Class.forName(methodFilterName).newInstance();
        methodInterceptor = (MethodInterceptor) Class.forName(methodInterceptorName).newInstance();
        repoQueryByMethodNameClazz = Class.forName(repoQueryByMethodNameClass);
        Collections.addAll(methodsOfSuperIntf, intfClazz.getMethods());
      } catch (Exception ex) {
        throw new KernelRuntimeException("Init Aop Configuration Fails!", ex);
      }
    }

    Collection<Method> filterParsableMethods(Method[] methods) {
      Set<Method> classMethods = new HashSet<>();
      Collections.addAll(classMethods, methods);
      return classMethods.stream().filter(method ->
          methodFilter.accept(method) == 0 && !methodsOfSuperIntf.contains(method)).collect(Collectors.toSet());
    }
  }

  Class build(Class clazz, CallbackFilter methodFilter, MethodInterceptor methodInterceptor) {
    Class repoClazz;
    try {
      if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
        repoClazz = CGLibUtils.buildProxyClass(clazz, methodInterceptor, methodFilter);
      } else {
        repoClazz = clazz.getSuperclass();
      }
    } catch (Exception e) {
      LogUtils.error(logger, e);
      throw new KernelRuntimeException(e);
    }
    return repoClazz;
  }
}