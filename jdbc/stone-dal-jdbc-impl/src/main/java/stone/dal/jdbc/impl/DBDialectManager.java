package stone.dal.jdbc.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stone.dal.jdbc.spi.DBDialectSpi;
import stone.dal.kernel.utils.LogUtils;
import stone.dal.kernel.utils.StringUtils;

import static stone.dal.kernel.utils.KernelUtils.replace;

/**
 * @author fengxie
 */
public class DBDialectManager {

	private Map<String, DBDialectSpi> dialectMapper = new HashMap<>();
	public static final String[] DB_TYPES = {"mysql", "oracle"};
	private static Logger logger = LoggerFactory.getLogger(DBDialectManager.class);

	private DBDialectManager() {
		InputStream errorInputStream = DBDialectManager.class.getResourceAsStream("/rdbms-errors.properties");
		Properties properties = new Properties();
		try {
			properties.load(errorInputStream);
			for (String dbType : DB_TYPES) {
				String className = "stone.dal.adaptor.spring.jdbc.autoconfigure.dialect." +
						StringUtils.firstChar2UpperCase(dbType) + "Dialect";
				Map<String, String> errors = filter(properties, dbType);
				DBDialectSpi dbDialect = (DBDialectSpi) Class.forName(className).getConstructor(Map.class).newInstance(errors);
				dialectMapper.put(dbDialect.getDbType(), dbDialect);
			}
		} catch (Exception e) {
			LogUtils.error(logger, e);
		}
	}

	public static DBDialectManager getInstance() {
		return DBDialectManagerHolder.sInstance;
	}

	public DBDialectSpi getDialect(String type) {
		return dialectMapper.get(type);
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> filter(Properties properties, String type) {
		Map<String, String> errors = new HashMap();
		Set keySet = properties.keySet();
		keySet.forEach(key -> {
			String strKey = (String) key;
			if (strKey.startsWith(type)) {
				errors.put(replace(strKey, type + ".", ""), properties.getProperty(strKey));
			}
		});
		return errors;
	}

	private static class DBDialectManagerHolder {
		private static DBDialectManager sInstance = new DBDialectManager();
	}
}
