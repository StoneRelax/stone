package stone.dal.common.api.annotation;

import java.lang.annotation.Inherited;

/**
 * <p>Title: Cache</p>
 * <p>Description: Cache</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: IPACS e-Solutions (S) Pte Ltd</p>
 *
 * @author feng.xie
 */
@Inherited
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.FIELD })
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Cache {

  String[] keys();
}
