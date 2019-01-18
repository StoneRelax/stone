package stone.dal.adaptor.spring.jdbc.aop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.parser.PartTree;

public class StJpaRepoMethodPartRegistry {

    private static final Logger logger = LoggerFactory.getLogger(StJpaRepoMethodPartRegistry.class);

    private static final StJpaRepoMethodPartRegistry _INSTANCE = new StJpaRepoMethodPartRegistry();

    public static StJpaRepoMethodPartRegistry getInstance() {
        return _INSTANCE;
    }

    private Map<String, PartTree> methodRegistry;

    private StJpaRepoMethodFilter methodFilter;

    private StJpaRepoMethodPartRegistry() {
        this.methodRegistry = new HashMap<>();
        methodFilter = new StJpaRepoMethodFilter();
    }

    public void registerMethod(Method method, Class doClazz) {
        if(methodFilter.accept(method) == 0){
            try {
                PartTree partTree = new PartTree(method.getName(), doClazz);
                String methodSignature = getMethodSignature(method);
                methodRegistry.put(methodSignature, partTree);
            } catch (Exception e){
                logger.error("Can not parse PartTree for method : " + getMethodSignature(method));
            }
        }
    }

    public PartTree getMethodPartTree(Method method){
        String methodSignature = getMethodSignature(method);
        if (methodRegistry.get(methodSignature) != null) {
            return methodRegistry.get(methodSignature);
        }else {
            return null;
        }
    }

    private String getMethodSignature(Method method){
        return method.getDeclaringClass().getName() + ":" + method.getName();
    }



}
