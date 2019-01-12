package stone.dal.models;

/**
 * @author fengxie
 */
public interface DalFieldMapper {

  <T> T getMapperVal(Object rowObj, String fieldName);

}
