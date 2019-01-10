package stone.dal.rdbms.impl.aop;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.cglib.proxy.CallbackFilter;

import javax.persistence.*;
import java.lang.reflect.Method;

/**
 * @author fengxie
 */
public class DalMethodFilter implements CallbackFilter {


	public int accept(Method method) {
		if (method.isAnnotationPresent(OneToMany.class)) {
			OneToMany relationMeta = method.getAnnotation(OneToMany.class);
			if (ArrayUtils.contains(relationMeta.cascade(), CascadeType.ALL)
					|| (ArrayUtils.contains(relationMeta.cascade(), CascadeType.REFRESH) && relationMeta.fetch() == FetchType.LAZY)) {
				return 0;
			}
		}
		if (method.isAnnotationPresent(OneToOne.class)) {
			OneToOne relationMeta = method.getAnnotation(OneToOne.class);
			if (ArrayUtils.contains(relationMeta.cascade(), CascadeType.ALL)
					|| (ArrayUtils.contains(relationMeta.cascade(), CascadeType.REFRESH) && relationMeta.fetch() == FetchType.LAZY)) {
				return 0;
			}
		}
		if (method.isAnnotationPresent(ManyToOne.class)) {
			ManyToOne relationMeta = method.getAnnotation(ManyToOne.class);
			if (ArrayUtils.contains(relationMeta.cascade(), CascadeType.ALL)
					|| (ArrayUtils.contains(relationMeta.cascade(), CascadeType.REFRESH) && relationMeta.fetch() == FetchType.LAZY)) {
				return 0;
			}
		}
		if (method.isAnnotationPresent(ManyToMany.class)) {
			ManyToMany relationMeta = method.getAnnotation(ManyToMany.class);
			if (ArrayUtils.contains(relationMeta.cascade(), CascadeType.ALL)
					|| (ArrayUtils.contains(relationMeta.cascade(), CascadeType.REFRESH) && relationMeta.fetch() == FetchType.LAZY)) {
				return 0;
			}
		}
		return 1;
	}
}