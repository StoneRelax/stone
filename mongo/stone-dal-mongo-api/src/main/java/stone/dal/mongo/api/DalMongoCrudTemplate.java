package stone.dal.mongo.api;

import stone.dal.common.api.DalObj;
import stone.dal.metadata.meta.Page;
import stone.dal.mongo.api.meta.MongoQueryMeta;

/**
 * Created by on 5/23/2017.
 */
public interface DalMongoCrudTemplate<T extends DalObj> {

	String create(T obj);

	String create(T obj, String schema);

	Page<Object> query(MongoQueryMeta mongoQueryMeta);

	T find(T obj);

	T find(T obj, String schema);

	void update(T obj);

	void update(T obj, String schema);

	void delById(T object);

	void delById(T object, String schema);

	void del(T object);

	void del(T object, String schema);

	void delAll(String collectName);

	void delAll(String collectName, String schema);
}
