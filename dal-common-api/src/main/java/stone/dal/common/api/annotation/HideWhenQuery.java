package stone.dal.common.api.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Component Name:Data dictionary
 * Description:Indicator of disable/enable cascade query
 *
 * @author feng.xie
 * @version $Revision: 1.1 $
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface HideWhenQuery {
}
