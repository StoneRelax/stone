package stone.dal.adaptor.spring.common.aop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.parser.PartTree;

public class StRepoMethodPartRegistry {

  private static final Logger logger = LoggerFactory.getLogger(StRepoMethodPartRegistry.class);

  private static StRepoMethodPartRegistry singleton = new StRepoMethodPartRegistry();

  public static StRepoMethodPartRegistry getInstance() {
    return singleton;
  }

  private Map<String, StRepoQueryByMethodName> methodRegistry = new HashMap<>();

  @SuppressWarnings("unchecked")
  public void registerMethod(Method method, Class doClazz, Class repoQueryClazz) {
    try {
      PartTree partTree = new PartTree(method.getName(), doClazz);
      String methodSignature = getMethodSignature(method);
      methodRegistry.put(methodSignature,
          (StRepoQueryByMethodName) repoQueryClazz.getConstructor(Method.class, PartTree.class)
              .newInstance(method, partTree));
    } catch (Exception e) {
      logger.error("Can not parse PartTree for method : " + getMethodSignature(method));
    }
  }

  public StRepoQueryByMethodName getQuery(Method method) {
    String methodSignature = getMethodSignature(method);
    StRepoQueryByMethodName repoQueryByMethodName = methodRegistry.get(methodSignature);
    if (repoQueryByMethodName != null) {
      return repoQueryByMethodName;
    } else {
      return null;
    }
  }

  private String getMethodSignature(Method method) {
    return method.getDeclaringClass().getName() + ":" + method.getName();
  }

}
