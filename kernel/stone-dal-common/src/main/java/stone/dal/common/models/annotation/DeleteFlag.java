package stone.dal.common.models.annotation;

import java.lang.annotation.Inherited;

/**
 * <p>Title: DeleteIndicators</p>
 * <p>Description: DeleteIndicators</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: Dr0ne Studio</p>
 *
 * @author feng.xie
 */
@Inherited
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.FIELD })
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface DeleteFlag {

  String field();
}
