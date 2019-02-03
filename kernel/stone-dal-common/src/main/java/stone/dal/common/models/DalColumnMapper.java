package stone.dal.common.models;

import stone.dal.common.models.data.BaseDo;

/**
 * @author fengxie
 */
public interface DalColumnMapper<V extends BaseDo, T> {

  T map(V rowObj, String column, String associateColumn, String args);

}
