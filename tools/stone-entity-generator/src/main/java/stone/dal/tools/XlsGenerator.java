package stone.dal.tools;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import stone.dal.models.meta.RelationMeta;
import stone.dal.models.meta.RelationTypes;
import stone.dal.tools.ex.AppException;
import stone.dal.tools.ex.BizException;
import stone.dal.tools.meta.DataFieldMeta;
import stone.dal.tools.meta.RawEntityMeta;
import stone.dal.tools.meta.RawRelationMeta;
import stone.dal.tools.utils.ClassUtilities;
import stone.dal.tools.utils.ExcelUtilities;
import stone.dal.tools.utils.StringUtilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static stone.dal.kernel.utils.KernelUtils.*;

public class XlsGenerator {


  public void build(File file) throws AppException {
    try {
      InputStream is = new FileInputStream(file);
      HSSFWorkbook book = ExcelUtilities.getWorkbook(is);
      int i = 0;
      List<RawEntityMeta> entities = new ArrayList<>();
      HashSet<String> n2nJoinTables = new HashSet<String>();
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
        meta.setNosql(ExcelUtilities.cellBool(headRow.getCell(3)));
        String delInd = ExcelUtilities.cellStr(headRow.getCell(9));
        if (!isStrEmpty(delInd)) {
          meta.setDelFlag(delInd);
        }
        String fileTag = ExcelUtilities.cellStr(headRow.getCell(11));
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
            if (beginRelationRead && (cell == null || isStrEmpty(ExcelUtilities.cellStr(cell)))) {
              break;
            }
            String sv = ExcelUtilities.cellStr(cell);
            if (sv.equals("<<Fields>>")) {
              beginFieldRead = true;
            } else if (sv.equals("<<Relations>>")) {
              beginFieldRead = false;
              beginRelationRead = true;
            } else if (!StringUtilities.isEmpty(sv)) {
              if (beginRelationRead && !replaceNull(ExcelUtilities.cellStr(sfRow.getCell(0))).equals("Type")) {
                RelationMeta relation = readRelations(meta, sfRow, n2nJoinTables);
                meta.getRelations().add(relation);
              } else if (beginFieldRead && !replaceNull(ExcelUtilities.cellStr(sfRow.getCell(0))).equals("Name")) {
                DataFieldMeta fieldMeta = readFields(entityName, sfRow);
                fieldMeta.setEntity(entityName);
                meta.getFields().add(fieldMeta);
              }
            }
          } else {
            if (beginRelationRead) {
              break;
            }
            if (row > 1000) {
              throw new AppException("No relation definition is found![" + entityName + "]");
            }
          }
          row++;
        }
        entities.add(meta);
        i++;
      }

    } catch (Exception e) {
      throw new AppException(e);
    }
  }


  private DataFieldMeta readFields(String entityName, HSSFRow sfRow) throws BizException, AppException {
    DataFieldMeta meta = new DataFieldMeta();
    String fieldName = ExcelUtilities.cellStr(sfRow.getCell(0)).trim();
    meta.setName(fieldName);
    String typeName = ExcelUtilities.cellStr(sfRow.getCell(1));
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
        List list = StringUtilities.splitString(property, ".");
        meta.setMaxlength(new Integer((String) list.get(0)));
      }
    }
    Boolean pk = ExcelUtilities.cellBool(sfRow.getCell(3));
    meta.setPk(pk);
    String seqType = ExcelUtilities.cellStr(sfRow.getCell(4));
    if (!isStrEmpty(seqType)) {
      if (seqType.contains(":")) {
        meta.setSeqType(str2Arr(seqType, ":")[0]);
        meta.setDefaultStartSeq(str2Arr(seqType, ":")[1]);
      } else {
        meta.setSeqType(seqType);
      }
    }
    if (boolValue(pk)) {
      meta.setNullable(false);
    } else {
      Boolean notNull = ExcelUtilities.cellBool(sfRow.getCell(5));
      meta.setNullable(!notNull);
    }
    meta.setWname(ExcelUtilities.cellStr(sfRow.getCell(6)));
    meta.setLabel(ExcelUtilities.cellStr(sfRow.getCell(7)));
    meta.setComboid(ExcelUtilities.cellStr(sfRow.getCell(8)));
    if (!isStrEmpty(meta.getComboid())) {
      if (isStrEmpty(meta.getWname())) {
        meta.setWname("form.combo");
      }
    }
    Boolean noDb = ExcelUtilities.cellBool(sfRow.getCell(9));
    meta.setNodb(noDb);
    String codes = ExcelUtilities.cellStr(sfRow.getCell(10));
    String codeTag = ExcelUtilities.cellStr(sfRow.getCell(11));
    String mapper = ExcelUtilities.cellStr(sfRow.getCell(12));
    String capital = ExcelUtilities.cellStr(sfRow.getCell(13));
    String order = ExcelUtilities.cellStr(sfRow.getCell(14));
    String wargs = ExcelUtilities.cellStr(sfRow.getCell(15));
    String unique = ExcelUtilities.cellStr(sfRow.getCell(16));
    String remarks = ExcelUtilities.cellStr(sfRow.getCell(17));
    String tags = ExcelUtilities.cellStr(sfRow.getCell(18));
    String dataAccess = ExcelUtilities.cellStr(sfRow.getCell(19));
    String comboFilter = ExcelUtilities.cellStr(sfRow.getCell(20));
    String index = ExcelUtilities.cellStr(sfRow.getCell(21));
    String constraints = ExcelUtilities.cellStr(sfRow.getCell(22));
    String fileField = ExcelUtilities.cellStr(sfRow.getCell(23));
    Boolean clob = ExcelUtilities.cellBool(sfRow.getCell(24));
    String dbName = ExcelUtilities.cellStr(sfRow.getCell(25));
    if (!isStrEmpty(codes) || !isStrEmpty(codeTag)) {
      meta.setMapper("code");
      meta.setCodeTags(codeTag);
      if (codeTag.contains(":")) {
        codeTag = str2Arr(codeTag, ":")[0];
      }
      meta.setNodb(true);
      meta.setWargs(codeTag);
      if (!isStrEmpty(codes)) {
        meta.setCodes(codes);
      }
    } else {
      if (mapper!=null) {
        if (mapper.contains("(") && mapper.contains(")")) {
          meta.setMapper(str2Arr(mapper, "(")[0]);
          String mappedBy = StringUtils.replace(mapper, meta.getMapper() + "(", "");
          mappedBy = StringUtils.replace(mappedBy, ")", "");
          meta.setMappedBy(mappedBy);
        } else {
          meta.setMapper(mapper);
        }
      }
    }
    if ("i18n".equals(wargs)) {
      meta.setI18n(true);
    }
    if (!isStrEmpty(capital)) {
      meta.setCapital(capital.toLowerCase());
    }
    if (!isStrEmpty(order)) {
      meta.setOrderBy(true);
      meta.setOrder(order.toLowerCase());
    }
    if (!isStrEmpty(wargs)) {
      meta.setWargs(wargs);
    } else {
      if ("datetime".equals(typeName) && (!"createDate".equals(meta.getName())
          && !"updateDate".equals(meta.getName()))) {
        meta.setWargs("withtimer=true");
      }
    }
    if (!isStrEmpty(unique)) {
      if (unique.equalsIgnoreCase("Y")) {
        meta.setUnique(entityName);
      } else {
        meta.setUnique(unique);
      }
    }
    if (!isStrEmpty(remarks)) {
      meta.setRemarks(remarks);
    }
    if (!isStrEmpty(tags)) {
      meta.setTags(tags);
    }
    if (!isStrEmpty(dataAccess)) {
      meta.setDataAccess(dataAccess);
    }
    if (isStrEmpty(property)) {
      if (!isStrEmpty(meta.getMappedBy())) {
        meta.setNodb(true);
      } else if ("string".equals(meta.getTypeName())) {
        meta.setMaxlength(150);
      }
    }
    if (!isStrEmpty(comboFilter)) {
      meta.setFilter(comboFilter);
    }
    if (!isStrEmpty(index) && !boolValue(meta.getNodb())) {
      meta.setIndex(index);
    }
    if (!isStrEmpty(constraints)) {
      meta.setConstraints(constraints);
    }
    if (!isStrEmpty(fileField)) {
      meta.setFileField("Y".equalsIgnoreCase(fileField));
    }
    if (boolValue(clob)) {
      meta.setClob(true);
    }
    if (!isStrEmpty(dbName)) {
      meta.setDbName(dbName);
    }
    addNormalAudit(meta);
    validFieldMeta(entityName, meta);
    return meta;
  }

  private RelationMeta readRelations(RawEntityMeta entityMeta, HSSFRow sfRow, HashSet<String> n2nJoinTables) {
    RawRelationMeta meta = new RawRelationMeta();
    meta.setJoinDomain(ExcelUtilities.cellStr(sfRow.getCell(1)));
    meta.setJoinProperty(ExcelUtilities.cellStr(sfRow.getCell(2)));
    meta.setJoinColumnName(ExcelUtilities.cellStr(sfRow.getCell(3)));
    String type = ExcelUtilities.cellStr(sfRow.getCell(0));
    if ("1:N".equals(type)) {
      type = RelationTypes.ONE_2_MANY.name();
    }
    if ("N:N".equals(type)) {
      String joinTable = ExcelUtilities.cellStr(sfRow.getCell(4));
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
    meta.setRelationType(type);
    return meta;
  }

  private void addNormalAudit(DataFieldMeta meta) {
    String suffix = "";
    if (Long.class.getName().equals(meta.getTypeName()) || "long".equals(meta.getTypeName())) {
      suffix = "Uuid";
    } else {
      if (boolValue(meta.getNodb())) {
        suffix = "~"; //mean skip createBy, updateBy
      }
    }
    boolean match = false;
    if (meta.getName().equals("createBy" + suffix)) {
      if (isStrEmpty(meta.getMapper()) || meta.getMapper().equals("user")) {
        meta.setSeqType("userCreate");
        match = true;
      }
    }
    if (meta.getName().equals("createDate")) {
      meta.setSeqType("createDate");
      match = true;
    }
    if (meta.getName().equals("updateBy" + suffix)) {
      if (isStrEmpty(meta.getMapper()) || meta.getMapper().equals("user")) {
        meta.setSeqType("userUpdate");
        meta.setWriteWhenNotEmpty(true);
        match = true;
      }
    }
    if (meta.getName().equals("updateDate")) {
      meta.setSeqType("updateDate");
      meta.setWriteWhenNotEmpty(true);
      match = true;
    }
    if (isStrEmpty(meta.getWname())) {
      if (match) {
        if (!meta.getName().toLowerCase().contains("uuid")) {
          meta.setWname("form.datalabel");
        }
      } else {
        if (meta.getName().equals("createBy") || meta.getName().equals("updateBy")) {
          meta.setWname("form.datalabel");
        }
      }
    }
  }

  private void validFieldMeta(String entityName, DataFieldMeta meta) throws AppException {
    if (StringUtilities.isEmpty(meta.getTypeName())) {
      throw new AppException("Entity:" + entityName + " Field:" + meta.getName() + "'type can not be null!");
    }
    if (StringUtilities.isEmpty(ClassUtilities.getPrimitiveClass(meta.getTypeName()))) {
      throw new AppException("Entity:" + entityName + " Field:" + meta.getName() + "'type is invalid!\n" +
          "Please use one of the following type" +
          "string,date,datetime,int,long,double,boolean,time");
    }
  }

}
