package stone.dal.jdbc.spring.adaptor.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.parser.PartTree;
import stone.dal.models.data.BaseDo;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class StJpaRepositoryMethodPartCache {

    private static final Logger logger = LoggerFactory.getLogger(StJpaRepositoryMethodPartCache.class);

    private static final StJpaRepositoryMethodPartCache _INSTANCE = new StJpaRepositoryMethodPartCache();

    public static StJpaRepositoryMethodPartCache getInstance(){
        return _INSTANCE;
    }

    private Map<String,PartTree> methodPartCache ;
    private StJpaRepositoryMethodFilter methodFilter;

    private StJpaRepositoryMethodPartCache(){
        this.methodPartCache = new HashMap<>();
        methodFilter = new StJpaRepositoryMethodFilter();
    }



    public void registMethod(Method method, Class doClazz){
        if(methodFilter.accept(method) == 0){
            try {
                PartTree partTree = new PartTree(method.getName(), doClazz);
                String methodSignature = getMethodSignature(method);
                methodPartCache.put(methodSignature,partTree);
            } catch (Exception e){
                logger.error("Can not parse PartTree for method : " + getMethodSignature(method));
            }
        }
    }

    public PartTree getMethodPartTree(Method method){
        String methodSignature = getMethodSignature(method);
        if(methodPartCache.get(methodSignature) != null){
            return methodPartCache.get(methodSignature);
        }else {
            return null;
        }
    }

    private String getMethodSignature(Method method){
        return method.getDeclaringClass().getName() + ":" + method.getName();
    }



}
