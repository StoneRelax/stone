package stone.dal.rdbms.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stone.dal.kernel.LogUtils;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static stone.dal.kernel.KernelUtils.str_2_arr;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;

/**
 * @author fengxie
 */
public abstract class RdbmsDataSourceManager {
	private Map<String, DataSource> dataSourceRegistry = new HashMap<>();
	private Map<String, ConnMeta> connMetaRegistry = new HashMap<>();
	private static Logger logger = LoggerFactory.getLogger(RdbmsDataSourceManager.class);

	@SuppressWarnings("unchecked")
	RdbmsDataSourceManager() {
		InputStream inputStream = RdbmsDataSourceManager.class.getResourceAsStream("/rdbms-data-source.properties");
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
				DataSource ds = DataSourceBuilder.create().url(connMeta.url).
						username(connMeta.userName).
						driverClassName(connMeta.driver).password(connMeta.pwd).build();
				connMetaRegistry.put(schema, connMeta);
				dataSourceRegistry.put(schema, ds);
			}
		} catch (Exception e) {
			LogUtils.error(logger, e);
			throw new RuntimeException(e);
		}
	}

	public Set<String> getSchemas() {
		return dataSourceRegistry.keySet();
	}

	public DataSource getDataSource(String schema) {
		return dataSourceRegistry.get(schema);
	}

	public String getType(String schema) {
		return connMetaRegistry.get(schema).type;
	}

	public static RdbmsDataSourceManager getInstance() {
		return RdbmsDataSourceProviderHolder.sInstance;
	}

	private static class RdbmsDataSourceProviderHolder {
		private static RdbmsDataSourceManager sInstance = new RdbmsDataSourceManager() {
		};
	}

	private class ConnMeta {
		String url;
		String userName;
		String pwd;
		String driver;
		String type;

		ConnMeta(Map<String, String> metaInfo) {
			this.url = metaInfo.get("url");
			this.pwd = metaInfo.get("pwd");
			this.userName = metaInfo.get("userName");
			this.driver = metaInfo.get("driver");
			this.type = metaInfo.get("type");
		}
	}
}
