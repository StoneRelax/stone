package stone.dal.ext.filer;

import java.io.InputStream;

public interface FileResolver {

  InputStream getInputStream(String uuid, String category);

  String resolve(InputStream is, String category);
}
