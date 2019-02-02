package stone.dal.ext.filer.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stone.dal.ext.filer.FileResolver;
import stone.dal.kernel.utils.FileUtils;
import stone.dal.kernel.utils.KernelRuntimeException;
import stone.dal.kernel.utils.LogUtils;
import stone.dal.kernel.utils.StringUtils;

public class LocalFileResolver implements FileResolver {

  private String storePath;

  private static Logger s_logger = LoggerFactory.getLogger(LocalFileResolver.class);

  public LocalFileResolver(String storePath) {
    this.storePath = storePath;
  }

  @Override
  public InputStream getInputStream(String uuid, String category) {
    String resolvePath = resolvePath(uuid, category);
    try {
      return new FileInputStream(resolvePath);
    } catch (FileNotFoundException e) {
      LogUtils.error(s_logger, e);
      throw new KernelRuntimeException(e);
    }
  }

  @Override
  public String resolve(InputStream is, String category) {
    try {
      String uuid = UUID.randomUUID().toString();
      FileUtils.writeFile(resolvePath(uuid, category), is);
      return uuid;
    } catch (Exception e) {
      LogUtils.error(s_logger, e);
      throw new KernelRuntimeException(e);
    }
  }

  private String resolvePath(String uuid, String category) {
    if (StringUtils.isEmpty(category)) {
      category = "anonymous ";
    }
    return storePath + "/" + category + "/" + uuid;
  }
}
