package stone.dal.mongo.api.meta;

import java.util.Map;
import org.bson.Document;
import stone.dal.mongo.api.DalMongoConstants;

/**
 * todo:description
 *
 * @author feng.xie
 * @version $Revision:
 */
public abstract class MongoQueryMeta {
    private String collection;
    private Document sort;
    private Document condition;
    private String schema = DalMongoConstants.PRIMARY_SCHEMA;
    private int pageNo = 1;
    private int pageSize = 50;
    private Class clazz = Map.class;

    public Class getClazz() {
        return clazz;
    }

    public int getPageNo() {
        return pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public String getCollection() {
        return collection;
    }

    public Document getSort() {
        return sort;
    }

    public Document getCondition() {
        return condition;
    }

    public String getSchema() {
        return schema;
    }

    public static Factory factory() {
        return new Factory();
    }

    public static class Factory {
        private MongoQueryMeta meta = new MongoQueryMeta() {
        };

        public Factory collection(String collection) {
            meta.collection = collection;
            return this;
        }

        public Factory sort(Document sort) {
            meta.sort = sort;
            return this;
        }

        public Factory condition(Document condition) {
            meta.condition = condition;
            return this;
        }

        public Factory pageNo(int pageNo) {
            meta.pageNo = pageNo;
            return this;
        }

        public Factory pageSize(int pageSize) {
            meta.pageSize = pageSize;
            return this;
        }

        public Factory schema(String schema) {
            meta.schema = schema;
            return this;
        }

        public Factory clazz(Class clazz) {
            meta.clazz = clazz;

            return this;
        }

        public MongoQueryMeta build() {
            return meta;
        }
    }
}

