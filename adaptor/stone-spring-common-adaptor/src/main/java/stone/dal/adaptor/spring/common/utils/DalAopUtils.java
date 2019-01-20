package stone.dal.adaptor.spring.common.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import stone.dal.common.StRepository;
import stone.dal.common.models.data.BaseDo;

public class DalAopUtils {

  public static Class getDoClass(Class repoClazz) {
    Class doClazz = null;
    Type genType = findRepoType(repoClazz);
    if (genType != null) {
      Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
      for (Type param : params) {
        if (((Class) param).getSuperclass() == BaseDo.class) {
          doClazz = (Class) param;
          break;
        }
      }
    }
    return doClazz;
  }

  private static Type findRepoType(Class repoClazz) {
    Type[] intfs = repoClazz.getGenericInterfaces();
    if (intfs != null) {
      for (Type intf : intfs) {
        //need to abstract abstract repo
        if (intf instanceof ParameterizedType && StRepository.class.isAssignableFrom(
            (Class<?>) ((ParameterizedType) intf).getRawType())) {
          return intf;
        } else {
          if (intf instanceof Class) {
            Type type = findRepoType((Class) intf);
            if (type != null) {
              return type;
            }
          }
        }
      }
    }
    return null;
  }
}
