package stone.dal.kernel;

import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.util.ClassUtils;

/**
 * @author fengxie
 */
public class CGLibUtils {
  private static ConcurrentHashMap<String, Class> proxyClazzPool = new ConcurrentHashMap<>();

  private static Logger logger = LoggerFactory.getLogger(CGLibUtils.class);

  private CGLibUtils() {
  }

  /**
   * Create CGLib Proxy object
   *
   * @param clazz             Model class
   * @param methodInterceptor Method interceptor
   * @param callBackFilter    Callback filter
   * @return Proxy object
   */
  private static Class buildProxy(
      Class clazz,
      MethodInterceptor methodInterceptor,
      CallbackFilter callBackFilter) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(clazz);
    enhancer.setCallbackTypes(new Class[] { methodInterceptor.getClass(), NoOp.class });
    enhancer.setCallbackFilter(callBackFilter);
    Class enhancedClass = enhancer.createClass();
    Callback[] callbacks = new Callback[] { methodInterceptor, NoOp.INSTANCE };
    Enhancer.registerStaticCallbacks(enhancedClass, callbacks);
    return enhancedClass;
  }

  public static Class getUserClass(Class clazz) {
    return ClassUtils.getUserClass(clazz);
  }

  public static Class getUserClass(Object obj) {
    return ClassUtils.getUserClass(obj);
  }
  public static Object buildProxyClass(
      Class clazz,
      MethodInterceptor methodInterceptor,
      CallbackFilter callBackFilter)
      throws IllegalAccessException, InstantiationException {
    String key =
        clazz.getName() + "." + methodInterceptor.getClass().getName() + "." + callBackFilter.getClass()
            .getName();
    Class enhancedClass = proxyClazzPool.computeIfAbsent(key, s -> {
      try {
        return buildProxy(clazz, methodInterceptor, callBackFilter);
      } catch (Exception e) {
        LogUtils.error(logger, e);
      }
      return null;
    });
    if (enhancedClass == null) {
      throw new IllegalAccessException("Initial class fails,[" + clazz.getName() + "]");
    }
    return enhancedClass.newInstance();
  }
}
