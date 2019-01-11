package stone.dal.mongo.impl;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import stone.dal.kernel.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static stone.dal.kernel.KernelUtils.str_2_arr;

/**
 * Created by on 2017/5/26.
 */
abstract class DalMongoDBManager {
	private Map<String, MongoDatabase> databaseRegistry = new HashMap<>();
	private static Logger logger = LoggerFactory.getLogger(DalMongoDBManager.class);

	@SuppressWarnings("unchecked")
	DalMongoDBManager() {
		InputStream inputStream = DalMongoDBManager.class.getResourceAsStream("/mongo-data-source.properties");
		try {
			Properties properties = new Properties();
			properties.load(inputStream);
			Set keySet = properties.keySet();
			Map<String, Map<String, String>> _metaInfoRegistry = new HashMap<>();
			for (String key : (Iterable<String>) keySet) {
				String[] keyInfo = str_2_arr(key, ".");
				Map<String, String> meta = _metaInfoRegistry.computeIfAbsent(keyInfo[0], k -> new HashMap<>());
				meta.put(keyInfo[1], (String) properties.get(key));
			}
			for (String schema : _metaInfoRegistry.keySet()) {
				ConnMeta connMeta = new ConnMeta(_metaInfoRegistry.get(schema));
				MongoClient client = new MongoClient(connMeta.ip, connMeta.port);
				databaseRegistry.put(schema, client.getDatabase(connMeta.name));
			}
		} catch (Exception e) {
			LogUtils.error(logger, e);
			throw new RuntimeException(e);
		}
	}

	MongoDatabase getDataSource(String schema) {
		return databaseRegistry.get(schema);
	}

	static DalMongoDBManager getInstance() {
		return MongoDBProviderHolder.sInstance;
	}

	private static class MongoDBProviderHolder {
		private static DalMongoDBManager sInstance = new DalMongoDBManager() {
		};
	}

	private class ConnMeta {
		String ip;
		int port;
		String name;

		ConnMeta(Map<String, String> metaInfo) {
			this.ip = metaInfo.get("ip");
			this.port = Integer.parseInt(metaInfo.get("port"));
			this.name = metaInfo.get("name");
		}
	}
}
