package stone.dal.adaptor.spring.common.aop;

import java.lang.reflect.Method;
import org.springframework.data.repository.query.parser.PartTree;
import stone.dal.adaptor.spring.common.utils.DalAopUtils;

public abstract class StRepoQueryByMethodName {

  protected PartTree tree;

  protected Class doClazz;

  public StRepoQueryByMethodName(Method method, PartTree tree) {
    doClazz = DalAopUtils.getDoClass(method.getDeclaringClass());
    this.tree = tree;
  }

  public abstract Object query(Method method, Object[] params);

}
