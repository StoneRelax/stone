package stone.dal.adaptor.spring.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import stone.dal.models.data.BaseDo;

public class DalAopUtils {

  public static Class getDoClass(Class repoClazz) {
    Class doClazz = null;
    Type[] genTypes = repoClazz.getGenericInterfaces();
    if (genTypes != null && genTypes.length != 0) {
      Type genType = genTypes[0];
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
}
