package stone.dal.adaptor.spring.jdbc.impl;

import org.aspectj.util.FileUtil;
import stone.dal.common.models.data.BaseDo;
import stone.dal.common.models.meta.EntityMeta;
import stone.dal.common.models.meta.FieldMeta;
import stone.dal.common.spi.ClobResolverSpi;
import stone.dal.kernel.utils.FileUtils;
import stone.dal.kernel.utils.KernelUtils;

import java.io.File;
import java.util.UUID;

public class DefaultClobResolverImpl implements ClobResolverSpi {

    private String localStorageDefaultRootPath;
    private static final String DIR_SEPARATOR = File.separator;

    public DefaultClobResolverImpl(String localStorageDefaultRootPath){
        this.localStorageDefaultRootPath = localStorageDefaultRootPath;
        setupDefaultLocalStorage();
    }

    @Override
    public String create(BaseDo obj, EntityMeta meta, String field) {
        UUID uuid = UUID.randomUUID();
        String dirPath = localStorageDefaultRootPath + DIR_SEPARATOR + meta.getTableName() + DIR_SEPARATOR + field;
        FileUtils.createDir(new File(dirPath));
        FileUtils.createFile(new File(dirPath + DIR_SEPARATOR + uuid.toString()));
        String content = KernelUtils.getPropVal(obj,field);
        FileUtils.writeFile(dirPath + DIR_SEPARATOR + uuid.toString(),content.getBytes());
        return uuid.toString();
    }

    @Override
    public void delete(BaseDo obj, EntityMeta meta, String field) {
        String uuid = KernelUtils.getPropVal(obj,field);
        String dirPath = localStorageDefaultRootPath + DIR_SEPARATOR + meta.getTableName() + DIR_SEPARATOR + field;
        FileUtils.deleteFile(dirPath + DIR_SEPARATOR + uuid);
    }

    @Override
    public String read(BaseDo obj, EntityMeta meta, String field) {
        String uuid = KernelUtils.getPropVal(obj,field);
        String dirPath = localStorageDefaultRootPath + DIR_SEPARATOR + meta.getTableName() + DIR_SEPARATOR + field;
        return new String(FileUtils.readFile(dirPath + DIR_SEPARATOR + uuid));
    }

    private void setupDefaultLocalStorage(){
        if(!FileUtils.isExisted(localStorageDefaultRootPath)){
            FileUtils.createDir(new File(localStorageDefaultRootPath));
        }
    }


}
