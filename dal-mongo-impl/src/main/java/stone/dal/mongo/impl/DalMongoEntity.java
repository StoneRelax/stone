package stone.dal.mongo.impl;

import stone.dal.common.DalEntity;
import stone.dal.common.api.meta.EntityMeta;
import stone.dal.common.api.meta.FieldMeta;

/**
 * Created by on 5/25/2017.
 */
public class DalMongoEntity extends DalEntity {
    public DalMongoEntity(EntityMeta meta) {
        super(meta);
    }

    @Override
    protected void doInit() {
    }

    @Override
    protected void readFieldInfo(FieldMeta fieldMeta) {
    }
}
