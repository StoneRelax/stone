package stone.dal.adaptor.spring.jdbc.impl;

import stone.dal.common.models.data.BaseDo;
import stone.dal.common.models.meta.EntityMeta;
import stone.dal.common.models.meta.FieldMeta;
import stone.dal.common.spi.ClobResolverSpi;
import stone.dal.common.spi.ResultSetClobHandler;
import stone.dal.kernel.utils.KernelUtils;

import java.util.List;

public class DefaultResultSetClobHandler implements ResultSetClobHandler {

    private ClobResolverSpi clobResolver;

    public DefaultResultSetClobHandler (ClobResolverSpi clobResolver){
        this.clobResolver = clobResolver;
    }

    @Override
    public void handle(List<BaseDo> resultSet, EntityMeta entityMeta) {
        for(FieldMeta fieldMeta : entityMeta.getFields()){
            if(fieldMeta.getClob()){
                resultSet.forEach(result->{
                    String content = clobResolver.read(result,entityMeta,fieldMeta.getName());
                    KernelUtils.setPropVal(result,fieldMeta.getName(),content);
                });
            }
        }
    }
}
