package stone.dal.adaptor.spring.jdbc.impl.aop;

import java.lang.reflect.Method;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.cglib.proxy.CallbackFilter;

/**
 * @author fengxie
 */
public class DoMethodFilter implements CallbackFilter {

	private boolean supportMarkDirty;

	public DoMethodFilter(boolean supportMarkDirty) {
		this.supportMarkDirty = supportMarkDirty;
	}

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
		if (supportMarkDirty) {
			String methodName = method.getName();
			if (methodName.startsWith("set") && !methodName.contains("_")) {
				return 0;
			}
		}
		return 1;
	}

	public static class DirtyMark extends DoMethodFilter {
		public DirtyMark() {
			super(true);
		}
	}

	public static class LazyLoad extends DoMethodFilter {
		public LazyLoad() {
			super(false);
		}
	}
}