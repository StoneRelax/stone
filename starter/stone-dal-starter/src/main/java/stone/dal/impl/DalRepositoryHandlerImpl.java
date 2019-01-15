package stone.dal.impl;

import stone.dal.kernel.utils.CGLibUtils;

import java.util.HashMap;
import java.util.Map;

public class DalRepositoryHandlerImpl {

    private DalRepositoryMethodInterceptor dalRepositoryMethodInterceptor;
    private DalMethodFilter dalMethodFilter;

    public DalRepositoryHandlerImpl() {
        this.dalRepositoryMethodInterceptor = new DalRepositoryMethodInterceptor();
        this.dalMethodFilter = new DalMethodFilter();
    }

    public Class build(Class clazz){
        Class repoClazz = null ;
            try {
                if(clazz.isInterface()){
                    repoClazz = CGLibUtils.buildProxyClass(clazz,dalRepositoryMethodInterceptor,dalMethodFilter);
                }else {
                    repoClazz = clazz.getSuperclass();
                }
            }catch (Exception e){
                // todo handle exception
            }
        return repoClazz;
    }
}
