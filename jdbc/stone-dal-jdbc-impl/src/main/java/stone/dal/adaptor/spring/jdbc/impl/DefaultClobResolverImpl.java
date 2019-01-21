package stone.dal.adaptor.spring.jdbc.impl;

import stone.dal.common.models.data.BaseDo;
import stone.dal.common.models.meta.EntityMeta;
import stone.dal.common.spi.ClobResolverSpi;

import java.util.UUID;

public class DefaultClobResolverImpl implements ClobResolverSpi {

    @Override
    public String create(BaseDo obj, EntityMeta meta, String field) {
        UUID uuid = UUID.randomUUID();
//        meta.getTableName()+"/"+ field "/" + uuid.toString()
        return uuid.toString();
    }

    @Override
    public void delete(BaseDo obj, EntityMeta meta, String field) {

    }

    @Override
    public String read(BaseDo obj, EntityMeta meta, String field) {
        return null;
    }
}
