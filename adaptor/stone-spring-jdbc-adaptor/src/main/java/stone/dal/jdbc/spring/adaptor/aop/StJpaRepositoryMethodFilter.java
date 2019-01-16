package stone.dal.jdbc.spring.adaptor.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.springframework.cglib.proxy.CallbackFilter;
import stone.dal.jdbc.api.StJpaRepository;

public class StJpaRepositoryMethodFilter implements CallbackFilter {

    public int accept(Method method) {
        if (Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())) {
            if (StJpaRepository.class.isAssignableFrom(method.getDeclaringClass())) {
                System.out.println(method);
                return 0;
            }
        }
        return 1;
    }
}
