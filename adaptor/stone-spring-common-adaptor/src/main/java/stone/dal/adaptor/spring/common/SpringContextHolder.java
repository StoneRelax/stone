package stone.dal.adaptor.spring.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public class SpringContextHolder {
  private static ApplicationContext applicationContext;

  public static void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    SpringContextHolder.applicationContext = applicationContext;
  }

  @SuppressWarnings("unchecked")
  public static <T> T getBean(String name) {
    checkApplicationContext();
    return (T) applicationContext.getBean(name);
  }

  @SuppressWarnings("unchecked")
  public static <T> T getBean(Class<T> clazz) {
    checkApplicationContext();
    return (T) applicationContext.getBean(clazz);
  }

  private static void checkApplicationContext() {
    if (applicationContext == null) {
      throw new IllegalStateException();
    }
  }
}
