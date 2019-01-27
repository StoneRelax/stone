package stone.dal.adaptor.spring.jdbc.impl;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import stone.dal.common.models.data.BaseDo;
import stone.dal.common.models.meta.EntityMeta;
import stone.dal.common.models.meta.FieldMeta;
import stone.dal.common.spi.ClobResolverSpi;
import stone.dal.kernel.utils.FileUtils;
import stone.dal.kernel.utils.KernelUtils;
import stone.dal.kernel.utils.StringUtils;

public class DefaultClobResolverImpl implements ClobResolverSpi {

  private String rootPath;

  public DefaultClobResolverImpl(String rootPath) {
    this.rootPath = rootPath;
    init();
  }

  @Override
  public void create(BaseDo obj, EntityMeta meta, String clobField) {
    String content = KernelUtils.getPropVal(obj, clobField);
    if (!StringUtils.isEmpty(content)) {
      FileUtils.writeFile(getPath(obj, meta, clobField), content.getBytes(
          StandardCharsets.UTF_8));
    }
  }

  @Override
  public void delete(BaseDo obj, EntityMeta meta, String clobField) {
    String path = getPath(obj, meta, clobField);
    FileUtils.deleteFile(path);
  }

  @Override
  public String read(BaseDo obj, EntityMeta meta, String clobField) {
    String path = getPath(obj, meta, clobField);
    byte[] content = FileUtils.readFile(path);
    if (content != null) {
      return new String(content, StandardCharsets.UTF_8);
    }
    return null;
  }

  private void init() {
    if (!FileUtils.isExisted(rootPath)) {
      FileUtils.createDir(new File(rootPath));
    }
  }

  private String getPath(BaseDo obj, EntityMeta meta, String clobField) {
    List<?> pks = meta.getFields().stream().filter(FieldMeta::getPk).
        map(fieldMeta -> KernelUtils.getPropVal(obj, fieldMeta.getName())).collect(Collectors.toList());
    String pkPath = StringUtils.combineString(pks, "/");
    return String.format("%s/%s/%s/%s.clob", rootPath, meta.getTableName(), pkPath, clobField);
  }
}
