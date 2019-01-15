package stone.dal.impl;

import stone.dal.kernel.utils.CGLibUtils;

import java.util.HashMap;
import java.util.Map;

public class DalRepositoryHandlerImpl {

    private DalRepositoryMethodInterceptor dalRepositoryMethodInterceptor;
    private DalMethodFilter dalMethodFilter;

    public DalRepositoryHandlerImpl(DalRepositoryMethodInterceptor dalRepositoryMethodInterceptor, DalMethodFilter dalMethodFilter) {
        this.dalRepositoryMethodInterceptor = dalRepositoryMethodInterceptor;
        this.dalMethodFilter = dalMethodFilter;
    }

    public Object build(Class clazz){
        Object repoObj = null ;
            try {
                if(clazz.isInterface()){
                    repoObj = CGLibUtils.enhanceObject(clazz,dalRepositoryMethodInterceptor,dalMethodFilter);
                }else {
                    repoObj = clazz.newInstance();
                }
            }catch (Exception e){
                // todo handle exception
            }
        return repoObj;
    }
}
