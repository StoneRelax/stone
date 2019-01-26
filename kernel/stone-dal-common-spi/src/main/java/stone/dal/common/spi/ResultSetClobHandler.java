package stone.dal.common.spi;

import stone.dal.common.models.data.BaseDo;
import stone.dal.common.models.meta.EntityMeta;

import java.util.List;

public interface ResultSetClobHandler {

    void handle(List<BaseDo> resultSet, EntityMeta entityMeta);
}
