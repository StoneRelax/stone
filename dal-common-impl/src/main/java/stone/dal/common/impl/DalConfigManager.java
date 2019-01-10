package stone.dal.common.impl;

import stone.dal.kernel.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * @author fengxie
 */
public class DalConfigManager {

	private Set<String> packages = new HashSet<>();
	private static Logger logger = LoggerFactory.getLogger(DalConfigManager.class);

	DalConfigManager() {
		try {
			Enumeration<URL> urls = DalConfigManager.class.getClassLoader().getResources("dal_config.properties");
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				Properties properties = new Properties();
				InputStream is = url.openStream();
				properties.load(is);
				String packageName = (String) properties.get("scan.package");
				if (packageName.contains(";")) {
					packages.addAll(Arrays.asList(packageName.split(";")));
				} else {
					packages.add(packageName);
				}

			}
		} catch (IOException e) {
			LogUtils.error(logger, e);
		}
	}

	public static DalConfigManager getInstance() {
		return DalConfigManagerHolder.manager;
	}

	public String[] getScanPackages() {
		return packages.toArray(new String[packages.size()]);
	}

	private static class DalConfigManagerHolder {
		private static DalConfigManager manager = new DalConfigManager() {
		};
	}

}
