package stone.dal.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import stone.dal.kernel.utils.ClassUtils;
import stone.dal.models.meta.FieldMeta;
import stone.dal.models.meta.RelationTypes;
import stone.dal.tools.meta.RawEntityMeta;
import stone.dal.tools.meta.RawFieldMeta;
import stone.dal.tools.meta.RawRelationMeta;
import stone.dal.tools.utils.ExcelUtils;

import static stone.dal.kernel.utils.KernelUtils.boolValue;
import static stone.dal.kernel.utils.KernelUtils.isStrEmpty;
import static stone.dal.kernel.utils.KernelUtils.replaceNull;
import static stone.dal.kernel.utils.KernelUtils.str2Arr;

public class DoGenerator {

  public void build(File file) throws Exception {
    InputStream is = new FileInputStream(file);
    HSSFWorkbook book = ExcelUtils.getWorkbook(is);
    int i = 0;
    List<RawEntityMeta> entities = new ArrayList<>();
    HashSet<String> n2nJoinTables = new HashSet<>();
    while (true) {
      HSSFSheet sheet = book.getSheetAt(0);
      if (i > 0) {
        try {
          sheet = book.getSheetAt(i);
        } catch (Exception e) {
          break;
        }
      }
      String entityName = sheet.getSheetName();
      if (isStrEmpty(entityName) || entityName.startsWith("Sheet")) {
        break;
      }
      if (entityName.endsWith(".draft")) {
        i++;
        continue;
      }
      RawEntityMeta meta = new RawEntityMeta();
      HSSFRow headRow = sheet.getRow(0);
      meta.setName(entityName);
      meta.setNosql(ExcelUtils.cellBool(headRow.getCell(3)));
      String delInd = ExcelUtils.cellStr(headRow.getCell(9));
      if (!isStrEmpty(delInd)) {
        meta.setDelFlag(delInd);
      }
      String fileTag = ExcelUtils.cellStr(headRow.getCell(11));
      if (!isStrEmpty(fileTag)) {
        meta.setFileFieldTags(fileTag);
      }
      int row = 1;
      boolean beginFieldRead = false;
      boolean beginRelationRead = false;
      while (true) {
        HSSFRow sfRow = sheet.getRow(row);
        int col = 0;
        if (sfRow != null) {
          HSSFCell cell = sfRow.getCell(col);
          if (beginRelationRead && (cell == null || isStrEmpty(ExcelUtils.cellStr(cell)))) {
            break;
          }
          String sv = ExcelUtils.cellStr(cell);
          if (sv.equals("<<Fields>>")) {
            beginFieldRead = true;
          } else if (sv.equals("<<Relations>>")) {
            beginFieldRead = false;
            beginRelationRead = true;
          } else if (!isStrEmpty(sv)) {
            if (beginRelationRead && !replaceNull(ExcelUtils.cellStr(sfRow.getCell(0))).equals("Type")) {
              RawRelationMeta relation = readRelations(meta, sfRow, n2nJoinTables);
              meta.getRawRelations().add(relation);
            } else if (beginFieldRead && !replaceNull(ExcelUtils.cellStr(sfRow.getCell(0))).equals("Name")) {
              FieldMeta fieldMeta = readFields(entityName, sfRow);
              meta.getFields().add(fieldMeta);
            }
          }
        } else {
          if (beginRelationRead) {
            break;
          }
          if (row > 1000) {
            throw new Exception("No relation definition is found![" + entityName + "]");
          }
        }
        row++;
      }
      entities.add(meta);
      i++;
    }
  }

  private FieldMeta readFields(String entityName, HSSFRow sfRow) throws Exception {
    RawFieldMeta meta = new RawFieldMeta();
    String fieldName = ExcelUtils.cellStr(sfRow.getCell(0)).trim();
    String typeName = ExcelUtils.cellStr(sfRow.getCell(1));
    meta.setName(fieldName);
    meta.setTypeName(typeName.toLowerCase());
    String property = sfRow.getCell(2).toString();
    if (!isStrEmpty(property)) {
      if (meta.getTypeName().equalsIgnoreCase("double")
          || meta.getTypeName().equalsIgnoreCase("long")
          || meta.getTypeName().equalsIgnoreCase("int")) {
        if (property.endsWith(".0")) {
          property = StringUtils.replace(property, ".0", "");
        }
        String[] fieldInfo = str2Arr(property, ",");
        meta.setPrecision(new Integer(fieldInfo[0]));
        if (fieldInfo.length > 1) {
          meta.setScale(new Integer(fieldInfo[1]));
        }
      } else {
        meta.setMaxLength(new Integer(str2Arr(property, ".")[0]));
      }
    }
    Boolean pk = ExcelUtils.cellBool(sfRow.getCell(3));
    meta.setPk(pk);
    String seqType = ExcelUtils.cellStr(sfRow.getCell(4));
    if (!isStrEmpty(seqType)) {
      if (seqType.contains(":")) {
        meta.setSeqType(str2Arr(seqType, ":")[0]);
        meta.setSeqStartNum(Integer.parseInt(str2Arr(seqType, ":")[1]));
      } else {
        meta.setSeqType(seqType);
      }
      meta.setSeqType(seqType);
    }
    if (boolValue(pk)) {
      meta.setNullable(false);
    } else {
      boolean notNull = ExcelUtils.cellBool(sfRow.getCell(5));
      meta.setNullable(!notNull);
    }
    Boolean noDb = ExcelUtils.cellBool(sfRow.getCell(9));
    meta.setNotPersist(noDb);
    String mapper = ExcelUtils.cellStr(sfRow.getCell(12));
    String order = ExcelUtils.cellStr(sfRow.getCell(14));
    String unique = ExcelUtils.cellStr(sfRow.getCell(16));
    String index = ExcelUtils.cellStr(sfRow.getCell(21));
    String constraints = ExcelUtils.cellStr(sfRow.getCell(22));
    String fileField = ExcelUtils.cellStr(sfRow.getCell(23));
    Boolean clob = ExcelUtils.cellBool(sfRow.getCell(24));
    String dbName = ExcelUtils.cellStr(sfRow.getCell(25));
    if (mapper != null) {
      if (mapper.contains("(") && mapper.contains(")")) {
        meta.setMapper(str2Arr(mapper, "(")[0]);
        String mappedBy = StringUtils.replace(mapper, meta.getMapper() + "(", "");
        mappedBy = StringUtils.replace(mappedBy, ")", "");
        meta.setMappedBy(mappedBy);
      } else {
        meta.setMapper(mapper);
      }
    }
    if (!isStrEmpty(order)) {
      meta.setOrder(order.toLowerCase());
    }
    if (!isStrEmpty(unique)) {
      if (unique.equalsIgnoreCase("Y")) {
        meta.setUnique(entityName);
      } else {
        meta.setUnique(unique);
      }
    }
    if (isStrEmpty(property)) {
      if (!isStrEmpty(meta.getMappedBy())) {
        meta.setNotPersist(true);
      } else if ("string".equals(meta.getTypeName())) {
        meta.setMaxLength(150);
      }
    }
    if (!isStrEmpty(index) && !boolValue(meta.getNotPersist())) {
      meta.setIndex(index);
    }
    if (!isStrEmpty(constraints)) {
      meta.setConstraints(constraints);
    }
    if (!isStrEmpty(fileField)) {
      meta.setFile("Y".equalsIgnoreCase(fileField));
    }
    if (boolValue(clob)) {
      meta.setClob(true);
    }
    if (!isStrEmpty(dbName)) {
      meta.setDbName(dbName);
    }
    validFieldMeta(entityName, meta);
    return meta;
  }

  private RawRelationMeta readRelations(RawEntityMeta entityMeta, HSSFRow sfRow, HashSet<String> n2nJoinTables) {
    RawRelationMeta meta = new RawRelationMeta();
    meta.setJoinDomain(ExcelUtils.cellStr(sfRow.getCell(1)));
    meta.setJoinProperty(ExcelUtils.cellStr(sfRow.getCell(2)));
    meta.setJoinColumnName(ExcelUtils.cellStr(sfRow.getCell(3)));
    String type = ExcelUtils.cellStr(sfRow.getCell(0));
    if ("1:N".equals(type)) {
      type = RelationTypes.ONE_2_MANY.name();
    }
    if ("N:N".equals(type)) {
      String joinTable = ExcelUtils.cellStr(sfRow.getCell(4));
      type = RelationTypes.MANY_2_MANY.name();
      if (isStrEmpty(joinTable)) {
        String reverseJoinTable = meta.getJoinDomain().toLowerCase() + "_" + entityMeta.getName().toLowerCase();
        if (!n2nJoinTables.contains(reverseJoinTable.toUpperCase())) {
          joinTable = entityMeta.getName().toLowerCase() + "_" + meta.getJoinDomain().toLowerCase();
        } else {
          joinTable = reverseJoinTable;
        }
      }
      meta.setJoinTable(joinTable.toUpperCase());
      n2nJoinTables.add(meta.getJoinTable());
    }
    if ("1:1".equals(type)) {
      type = RelationTypes.ONE_2_ONE_REF.name();
      meta.setUpdatable(false);
    }
    meta.setRelationType(RelationTypes.valueOf(type));
    return meta;
  }

  private void validFieldMeta(String entityName, RawFieldMeta meta) throws Exception {
    if (isStrEmpty(meta.getTypeName())) {
      throw new Exception("Entity:" + entityName + " Field:" + meta.getName() + "'type can not be null!");
    }
    if (ClassUtils.isPrimitive(Class.forName(meta.getTypeName()))) {
      throw new Exception("Entity:" + entityName + " Field:" + meta.getName() + "'type is invalid!\n" +
          "Please use one of the following type" +
          "string,date,datetime,int,long,double,boolean,time");
    }
  }

}
