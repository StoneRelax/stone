package stone.dal.adaptor.spring.jdbc.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.springframework.cglib.proxy.CallbackFilter;
import stone.dal.adaptor.spring.jdbc.api.StJpaRepository;

public class StJpaRepoMethodFilter implements CallbackFilter {

    public int accept(Method method) {
        if (Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())) {
            if (StJpaRepository.class.isAssignableFrom(method.getDeclaringClass())) {
                return 0;
            }
        }
        return 1;
    }
}
