package stone.dal.common.models;

/**
 * @author fengxie
 */
public interface DalFieldMapper {

  <T> T getMapperVal(Object rowObj, String fieldName);

}
