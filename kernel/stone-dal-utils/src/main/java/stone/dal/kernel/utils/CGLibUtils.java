package stone.dal.kernel.utils;

import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.NoOp;

/**
 * @author fengxie
 */
public class CGLibUtils {
  private static ConcurrentHashMap<String, Class> proxyClazzPool = new ConcurrentHashMap<>();

  private static Logger logger = LoggerFactory.getLogger(CGLibUtils.class);

  private CGLibUtils() {
  }

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

  public static Class enhance(
      Class superClass,
      MethodInterceptor methodInterceptor,
      CallbackFilter callBackFilter)
      throws IllegalAccessException {
    String key =
        superClass.getName() + "." + methodInterceptor.getClass().getName() + "." + callBackFilter.getClass()
            .getName();
    Class enhancedClass = proxyClazzPool.computeIfAbsent(key, s -> {
      try {
        return buildProxy(superClass, methodInterceptor, callBackFilter);
      } catch (Exception e) {
        LogUtils.error(logger, e);
      }
      return null;
    });
    if (enhancedClass == null) {
      throw new IllegalAccessException("Initial class fails,[" + superClass.getName() + "]");
    }
    return enhancedClass;
  }
}
