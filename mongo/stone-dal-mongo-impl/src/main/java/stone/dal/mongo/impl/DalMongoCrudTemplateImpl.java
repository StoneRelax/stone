package stone.dal.mongo.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import stone.dal.common.api.DalEntityMetaManager;
import stone.dal.common.api.BaseDo;
import stone.dal.common.api.meta.EntityMeta;
import stone.dal.common.api.meta.UniqueIndexMeta;
import stone.dal.kernel.ConvertUtils;
import stone.dal.kernel.KernelRuntimeException;
import stone.dal.kernel.StringUtils;
import stone.dal.metadata.meta.Page;
import stone.dal.mongo.api.DalMongoConstants;
import stone.dal.mongo.api.DalMongoCrudTemplate;
import stone.dal.mongo.api.meta.MongoQueryMeta;
import stone.dal.spi.DalSequenceSpi;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static stone.dal.kernel.KernelUtils.get_v;
import static stone.dal.kernel.KernelUtils.list_emp;
import static stone.dal.kernel.KernelUtils.set_v;


/**
 * Created by on 5/23/2017.
 */
public class DalMongoCrudTemplateImpl<T extends BaseDo> implements DalMongoCrudTemplate<T> {
	private DalSequenceSpi dalSequenceSpi;
	private DalEntityMetaManager dalEntityMetaManager;

	public DalMongoCrudTemplateImpl(
			DalEntityMetaManager dalEntityMetaManager, DalSequenceSpi dalSequenceSpi) {
		this.dalEntityMetaManager = dalEntityMetaManager;
		this.dalSequenceSpi = dalSequenceSpi;
	}

	private void bindPkValues(T obj, DalMongoEntity entity) {
		if (dalSequenceSpi != null) {
			entity.getPks().forEach(pkField -> {
				Object v = get_v(obj, pkField);
				if (v == null) {
					v = dalSequenceSpi.next(obj, pkField);
					set_v(obj, pkField, v);
				}
			});
		}
	}

	private MongoDatabase getDB(String schema) {
		if (schema == null) {
			schema = DalMongoConstants.PRIMARY_SCHEMA;
		}
		return DalMongoDBManager.getInstance().getDataSource(schema);
	}

	@Override
	public String create(T obj) {
		return create(obj, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String create(T obj, String schema) {
		ensureUnique(obj, schema);
		Class clazz = obj.getClass();
		EntityMeta entityMeta = dalEntityMetaManager.getEntity(clazz);
		DalMongoEntity mongoEntity = new DalMongoEntity(entityMeta);

		String collection = entityMeta.getTableName();
		MongoCollection dbCollection = getDB(schema).getCollection(collection);
		bindPkValues(obj, mongoEntity);
		Object _id = mongoEntity.getPkValues(obj)[0];

		Document dbObj = new Document(ConvertUtils.obj2Map(obj, ConvertUtils.Factory.getInstance().saveTimeStampAsLong(true).build()));
		dbObj.put("_id", _id.toString());
		dbCollection.insertOne(dbObj);
		return _id.toString();
	}

	@Override
	public Page<Object> query(MongoQueryMeta queryMeta) {
		Page<Object> page = new Page<>();
		List<Object> _list = new ArrayList<>();
		String schema = queryMeta.getSchema();
		String collection = queryMeta.getCollection();
		if (collection == null && queryMeta.getClazz() != null) {
			EntityMeta entityMeta = dalEntityMetaManager.getEntity(queryMeta.getClazz());
			collection = entityMeta.getTableName();
		}
		MongoCollection dbCollection = getDB(schema).getCollection(collection);
		MongoCursor cur;
		FindIterable it;
		int totalCount;
		if (queryMeta.getCondition() != null) {
			totalCount = (int) dbCollection.count(queryMeta.getCondition());
		} else {
			totalCount = (int) dbCollection.count();
		}
		int pageNo = queryMeta.getPageNo();
		int pageSize = queryMeta.getPageSize();
		while (pageNo > 0) {
			if (queryMeta.getCondition() != null) {
				it = dbCollection.find(queryMeta.getCondition()).skip((pageNo - 1) * pageSize).limit(pageSize);
			} else {
				it = dbCollection.find().skip((pageNo - 1) * pageSize).limit(pageSize);
			}
			int totalPage = (totalCount + pageSize - 1) / pageSize;
			if (!StringUtils.isEmpty(queryMeta.getSort())) {
				it = it.sort(queryMeta.getSort());
			}
			cur = it.iterator();
			while (cur.hasNext()) {
				Map<String, Object> row = (Document) cur.next();
				if (queryMeta.getClazz() != null) {
					Object obj = ConvertUtils.map2Obj(
							row, queryMeta.getClazz(), ConvertUtils.Factory.getInstance().saveTimeStampAsLong(true).build());
					_list.add(obj);
				} else {
					_list.add(row);
				}
			}
			if (!list_emp(_list)) {
				page.setRows(_list);
				page.setPageNo(pageNo);
				page.setTotal(totalPage);
				page.setTotalCount(totalCount);
				break;
			}
			pageNo--;
		}
		if (list_emp(_list)) {
			page.setPageNo(0);
		}
		return page;
	}

	public T find(T obj) {
		return find(obj, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T find(T obj, String schema) {
		T res = null;
		Document resMap = null;
		EntityMeta entityMeta = dalEntityMetaManager.getEntity(obj.getClass());
		String collection = entityMeta.getTableName();
		MongoCollection dbCollection = getDB(schema).getCollection(collection);

		Document findObj = new Document();
		Map<String, Object> objMap = ConvertUtils.obj2Map(obj, ConvertUtils.Factory.getInstance().saveTimeStampAsLong(true).build());
		findObj.putAll(objMap);
		MongoCursor cur = dbCollection.find(findObj).iterator();
		if (cur.hasNext()) {
			resMap = (Document) cur.next();
		}
		if (resMap != null) {
			res = (T) ConvertUtils.map2Obj(resMap, obj.getClass(), ConvertUtils.Factory.getInstance().saveTimeStampAsLong(true).build());
		}
		return res;
	}

	@Override
	public void update(T obj) {
		update(obj, null);
	}

	@Override
	public void update(T obj, String schema) {
		EntityMeta entityMeta = dalEntityMetaManager.getEntity(obj.getClass());
		DalMongoEntity mongoEntity = new DalMongoEntity(entityMeta);
		String collection = entityMeta.getTableName();
		MongoCollection dbCollection = getDB(schema).getCollection(collection);

		String id = (String) mongoEntity.getPkValues(obj)[0];
		Document findObj = new Document();
		findObj.put("_id", id);

		Document updateObj = new Document();
		Document dbObj = new Document(ConvertUtils.obj2Map(obj, ConvertUtils.Factory.getInstance().saveTimeStampAsLong(true).build()));
		updateObj.put("$set", dbObj);
		dbCollection.updateOne(findObj, updateObj);
	}

	@Override
	public void delById(T obj) {
		delById(obj, null);
	}

	@Override
	public void delById(T obj, String schema) {
		Class clazz = obj.getClass();
		EntityMeta entityMeta = dalEntityMetaManager.getEntity(clazz);
		DalMongoEntity mongoEntity = new DalMongoEntity(entityMeta);
		String collection = entityMeta.getTableName();
		MongoCollection dbCollection = getDB(schema).getCollection(collection);

		Object id = mongoEntity.getPkValues(obj)[0];
		if (id == null) {
			throw new KernelRuntimeException("Calling delById fails due to id is required when object is deleted from mongo");
		}
		Document dbObj = new Document();
		dbObj.put("_id", id.toString());
		dbCollection.deleteOne(dbObj);
	}

	@Override
	public void del(T obj) {
		del(obj, null);
	}

	@Override
	public void del(T obj, String schema) {
		Class clazz = obj.getClass();
		EntityMeta entityMeta = dalEntityMetaManager.getEntity(clazz);
		DalMongoEntity mongoEntity = new DalMongoEntity(entityMeta);
		String collection = entityMeta.getTableName();
		MongoCollection dbCollection = getDB(schema).getCollection(collection);

		Document findObj = new Document();
		Map<String, Object> objMap = ConvertUtils.obj2Map(obj,
				ConvertUtils.Factory.getInstance().saveTimeStampAsLong(true).build());
		findObj.putAll(objMap);
		dbCollection.deleteOne(findObj);
	}

	@Override
	public void delAll(String collectName) {
		delAll(collectName, null);
	}

	@Override
	public void delAll(String collectName, String schema) {
		MongoCollection dbCollection = getDB(schema).getCollection(collectName);
		BasicDBObject filterObj = new BasicDBObject();
		dbCollection.deleteMany(filterObj);
	}

	private void ensureUnique(T domain, String schema) {
		Class clazz = domain.getClass();
		EntityMeta entityMeta = dalEntityMetaManager.getEntity(clazz);
		DalMongoEntity mongoEntity = new DalMongoEntity(entityMeta);
		Object exist = readByUnique(domain, schema);
		if (exist == null) {
			try {
				Collection<String> pks = mongoEntity.getPks();
				T findObj = (T) clazz.newInstance();
				boolean noPkValue = true;
				for (String pk : pks) {
					Object v = get_v(domain, pk);
					if (v != null) {
						set_v(findObj, pk, v);
						noPkValue = false;
					}
				}
				if (!noPkValue) {
					exist = find(findObj, schema);
				}
			} catch (InstantiationException | IllegalAccessException e) {
				throw new KernelRuntimeException(e.getMessage());
			}
		}
		if (exist != null) {
			throw new KernelRuntimeException("error.integrity.violation");
		}
	}

	@SuppressWarnings("unchecked")
	private T readByUnique(T domain, String schema) {
		T exist = null;
		Class clazz = domain.getClass();
		EntityMeta entityMeta = dalEntityMetaManager.getEntity(clazz);
		Collection<UniqueIndexMeta> uniqueIndices = entityMeta.getUniqueIndices();
		for (UniqueIndexMeta uniqueIndex : uniqueIndices) {
			try {
				String[] cols = uniqueIndex.getColumnNames();
				T findObj = (T) clazz.newInstance();
				boolean noUniqueVal = true;
				for (String col : cols) {
					Object v = get_v(domain, col);
					if (v != null) {
						set_v(findObj, col, v);
						noUniqueVal = false;
					}
				}
				if (!noUniqueVal) {
					exist = find(findObj, schema);
					if (exist != null) {
						break;
					}
				}
			} catch (InstantiationException | IllegalAccessException e) {
				throw new KernelRuntimeException(e);
			}
		}
		return exist;
	}
}
