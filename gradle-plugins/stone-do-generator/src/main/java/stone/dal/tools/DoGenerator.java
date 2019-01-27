package stone.dal.tools;

import de.hunsicker.jalopy.Jalopy;
import freemarker.cache.URLTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stone.dal.common.models.meta.EntityMeta;
import stone.dal.common.models.meta.FieldMeta;
import stone.dal.common.models.meta.RelationMeta;
import stone.dal.common.models.meta.RelationTypes;
import stone.dal.common.models.meta.UniqueIndexMeta;
import stone.dal.kernel.utils.ClassUtils;
import stone.dal.tools.meta.ExcelSpecColumnEnu;
import stone.dal.tools.meta.RawEntityMeta;
import stone.dal.tools.meta.RawFieldMeta;
import stone.dal.tools.meta.RawRelationMeta;
import stone.dal.tools.utils.ExcelUtils;

import static stone.dal.kernel.utils.KernelUtils.boolValue;
import static stone.dal.kernel.utils.KernelUtils.isStrEmpty;
import static stone.dal.kernel.utils.KernelUtils.replace;
import static stone.dal.kernel.utils.KernelUtils.replaceNull;
import static stone.dal.kernel.utils.KernelUtils.str2Arr;

public class DoGenerator {
  private static Map<String, Class> classTypeMap = new ConcurrentHashMap<>();

  private static Logger s_logger = LoggerFactory.getLogger(DoGenerator.class);

  static {
    classTypeMap.put(String.class.getName(), String.class);
    classTypeMap.put(BigDecimal.class.getName(), BigDecimal.class);
    classTypeMap.put(Double.class.getName(), Double.class);
    classTypeMap.put(Date.class.getName(), Date.class);
    classTypeMap.put(Integer.class.getName(), Integer.class);
    classTypeMap.put(Timestamp.class.getName(), Timestamp.class);
    classTypeMap.put(int.class.getName(), int.class);
    classTypeMap.put(long.class.getName(), long.class);
    classTypeMap.put(Long.class.getName(), Long.class);
    classTypeMap.put(boolean.class.getName(), boolean.class);
    classTypeMap.put(Boolean.class.getName(), Boolean.class);
    classTypeMap.put(Class.class.getName(), Class.class);
    classTypeMap.put(XMLGregorianCalendar.class.getName(), XMLGregorianCalendar.class);
    classTypeMap.put("java.lang.Object", Object.class);
    classTypeMap.put(Byte.class.getName(), Byte.class);
    classTypeMap.put(byte.class.getName(), byte.class);
    classTypeMap.put("string", String.class);
    classTypeMap.put("date", Date.class);
    classTypeMap.put("datetime", Timestamp.class);
    classTypeMap.put("int", Integer.class);
    classTypeMap.put("long", Long.class);
    classTypeMap.put("double", BigDecimal.class);
    classTypeMap.put("boolean", Boolean.class);
    classTypeMap.put("time", String.class);
  }

  public void build(String xlsxPath, String targetSource, String rootPackage) throws Exception {
    File sourceExcelFile = new File(xlsxPath);
    List<RawEntityMeta> entityMetas = parseFile(sourceExcelFile);
    String pojoPath = targetSource != null ? targetSource : "gen-src/";
    writeDoFiles(entityMetas, pojoPath, rootPackage + ".jpa");
    writeRepositoryFiles(entityMetas, pojoPath, rootPackage + ".repo");
    writeControllerFiles(entityMetas, pojoPath, rootPackage + ".controller");
  }

  private void writeControllerFiles(List<RawEntityMeta> entityMetas, String pojoPath, String packageName) throws Exception {
    //todo:2. write a rest controller sample, stone.dal.adaptor.spring.jdbc.aop.example.PersonService
    packageName = packageName == null ? "stone.dal.pojo" : packageName;
    List<String> contents = createContollerJavaSource(entityMetas, packageName);
    String javaFile;
    for (int i = 0; i < contents.size(); i++) {
      String content = contents.get(i);
      javaFile = pojoPath + "src/main/java/" + replace(packageName, ".", "/")
              + "/" + ExcelUtils.convertFirstAlphetUpperCase(entityMetas.get(i).getName())+"Controller" + ".java";
      ExcelUtils.writeFile(javaFile, content.getBytes());
//      Jalopy codeFormatter = new Jalopy();
//      StringBuffer output = new StringBuffer();
//      codeFormatter.setInput(new File(javaFile));
//      codeFormatter.setOutput(output);
//      codeFormatter.format();
//      ExcelUtils.writeFile(javaFile, output.toString().getBytes());
    }
  }

  private void writeRepositoryFiles(List<RawEntityMeta> entityMetas, String pojoPath, String packageName) throws Exception{
    //todo:1. write a template sample, stone.dal.adaptor.spring.jdbc.aop.example.repo.PersonRepository
    packageName = packageName == null ? "stone.dal.pojo" : packageName;
    List<String> contents = createRepoJavaSource(entityMetas, packageName);
    String javaFile;
    for (int i = 0; i < contents.size(); i++) {
      String content = contents.get(i);
      javaFile = pojoPath + "src/main/java/" + replace(packageName, ".", "/")
              + "/" + ExcelUtils.convertFirstAlphetUpperCase(entityMetas.get(i).getName())+"Repository" + ".java";
      ExcelUtils.writeFile(javaFile, content.getBytes());
//      Jalopy codeFormatter = new Jalopy();
//      StringBuffer output = new StringBuffer();
//      codeFormatter.setInput(new File(javaFile));
//      codeFormatter.setOutput(output);
//      codeFormatter.format();
//      ExcelUtils.writeFile(javaFile, output.toString().getBytes());
    }
  }



  private void writeDoFiles(List<RawEntityMeta> entityMetas, String pojoPath, String packageName)
      throws Exception {
    packageName = packageName == null ? "stone.dal.pojo" : packageName;
    List<String> contents = createDoJavaSource(entityMetas, packageName);
    String javaFile;
    for (int i = 0; i < contents.size(); i++) {
      String content = contents.get(i);
      javaFile = pojoPath + "src/main/java/" + replace(packageName, ".", "/")
          + "/" + ExcelUtils.convertFirstAlphetUpperCase(entityMetas.get(i).getName()) + ".java";
      ExcelUtils.writeFile(javaFile, content.getBytes());
//      Jalopy codeFormatter = new Jalopy();
//      StringBuffer output = new StringBuffer();
//      codeFormatter.setInput(new File(javaFile));
//      codeFormatter.setOutput(output);
//      codeFormatter.format();
//      ExcelUtils.writeFile(javaFile, output.toString().getBytes());
    }
  }

  public boolean hasUniqueKeys(RawEntityMeta entityMeta) {
    return CollectionUtils.isNotEmpty(entityMeta.getUniqueIndices());
  }

  public Set<String> uniqueIndices(EntityMeta entityMeta) {
    return entityMeta.getUniqueIndices().stream().map(UniqueIndexMeta::getName).collect(Collectors.toSet());
  }

  public String[] getUniqueColumns(EntityMeta entityMeta, String idxName) {
    Optional<UniqueIndexMeta> optional = entityMeta.getUniqueIndices().stream()
        .filter(index -> idxName.equals(index.getName())).findFirst();
    return optional.map(UniqueIndexMeta::getColumnNames).orElse(null);
  }

  public String dbIdxName(String idxName) {
    return "idx_" + idxName;
  }

  private List<RawEntityMeta> parseFile(File file) throws Exception {
    InputStream is = new FileInputStream(file);
    Workbook book = ExcelUtils.getWorkbook(is);
    int i = 0;
    List<RawEntityMeta> entities = new ArrayList<>();
    HashSet<String> n2nJoinTables = new HashSet<>();
    while (true) {
      Sheet sheet = book.getSheetAt(0);
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
      Row headRow = sheet.getRow(0);
      meta.setName(entityName);
      meta.setNosql(ExcelUtils.cellBool(headRow.getCell(3)));
      String delInd = ExcelUtils.cellStr(headRow.getCell(9));
      if (!isStrEmpty(delInd)) {
        meta.setDelFlag(delInd);
      }
      int row = 1;
      boolean beginFieldRead = false;
      boolean beginRelationRead = false;
      while (true) {
        Row sfRow = sheet.getRow(row);
        int col = 0;
        if (sfRow != null) {
          Cell cell = sfRow.getCell(col);
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
              RawFieldMeta fieldMeta = readField(entityName, sfRow);
              meta.getRawFields().add(fieldMeta);
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
      resolveFileFields(meta);
      entities.add(buildUniqueIndices(meta));
      i++;
    }
    return entities;
  }

  private void resolveFileFields(RawEntityMeta meta) {
    List<RawFieldMeta> fileUuidFields = meta.getRawFields().stream().filter(rawFieldMeta -> rawFieldMeta.getFile()).map(
        rawFieldMeta -> {
          RawFieldMeta fileUuidField = new RawFieldMeta();
          fileUuidField.setTypeName("string");
          fileUuidField.setMaxLength(64);
          fileUuidField.setDbName(rawFieldMeta.getName() + "_uuid");
          fileUuidField.setName(rawFieldMeta.getName() + "Uuid");
          return fileUuidField;
        }
    ).collect(Collectors.toList());
    meta.getRawFields().addAll(fileUuidFields);
  }

  private RawEntityMeta buildUniqueIndices(RawEntityMeta meta) {
    Map<String, List<String>> uniqueIndexMetaMap = new HashMap<>();
    meta.getRawFields().forEach(fieldMeta -> {
      if (StringUtils.isNotEmpty(fieldMeta.getUnique())) {
        if (uniqueIndexMetaMap.containsKey(fieldMeta.getUnique())) {
          uniqueIndexMetaMap.get(fieldMeta.getUnique()).add(fieldMeta.getDbName());
        } else {
          List<String> columns = new ArrayList<>();
          columns.add(fieldMeta.getDbName());
          uniqueIndexMetaMap.put(fieldMeta.getUnique(), columns);
        }
      }
    });
    uniqueIndexMetaMap.forEach((unique, columns) -> {
      meta.getUniqueIndices().add(new UniqueIndexMeta(columns.toArray(new String[0]), unique));
    });
    return meta;
  }

  public List<String> createDoJavaSource(List<RawEntityMeta> entities, String packageName) throws Exception {
    Map<String, RawEntityMeta> mapper = entities.stream()
        .collect(Collectors.toMap(RawEntityMeta::getName, entityMeta -> entityMeta));
    List<String> javaContents = new ArrayList<>();
    for (RawEntityMeta entityMeta : entities) {
      String javaContent = genDoJavaClass(entityMeta, packageName, mapper);
      javaContents.add(javaContent);
    }
    return javaContents;
  }

  public List<String> createRepoJavaSource(List<RawEntityMeta> entities, String packageName) throws Exception {
    List<String> javaContents = new ArrayList<>();
    for (RawEntityMeta entityMeta : entities) {
      String javaContent = genRepoJavaClass(entityMeta, packageName);
      javaContents.add(javaContent);
    }
    return javaContents;
  }

  public List<String> createContollerJavaSource(List<RawEntityMeta> entities, String packageName) throws Exception {
    List<String> javaContents = new ArrayList<>();
    for (RawEntityMeta entityMeta : entities) {
      String javaContent = genControllerJavaClass(entityMeta, packageName);
      javaContents.add(javaContent);
    }
    return javaContents;
  }

  private String genControllerJavaClass(RawEntityMeta entityMeta, String packageName) throws Exception{
    Configuration cfg = new Configuration();
    cfg.setTemplateLoader(
            new URLTemplateLoader() {
              protected URL getURL(String name) {
                Locale locale = Locale.getDefault();
                String urlName =
                        "stone/dal/tools/template/" + StringUtils.replace(name, "_" + locale.toString(), "");
                return Thread.currentThread().getContextClassLoader().getResource(urlName);
              }
            });
    cfg.setObjectWrapper(new DefaultObjectWrapper());
    try {
      Template temp = cfg.getTemplate("controller_java.ftl");
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      Writer out = new OutputStreamWriter(bos);
      SimpleHash params = new SimpleHash();
      params.put("className", entityMeta.getName()+"Controller");
      params.put("packageName", stone.dal.kernel.utils.StringUtils.replaceNull(packageName));
      params.put("repoClass", entityMeta.getName()+"Repository");
      temp.process(params, out);
      out.flush();
      return new String(bos.toByteArray(), StandardCharsets.UTF_8);
    } catch (Exception e) {
      s_logger.error(e.getMessage());
      throw new Exception(e);
    }
  }


  private String genRepoJavaClass(RawEntityMeta entityMeta, String packageName) throws Exception{
    Configuration cfg = new Configuration();
    cfg.setTemplateLoader(
            new URLTemplateLoader() {
              protected URL getURL(String name) {
                Locale locale = Locale.getDefault();
                String urlName =
                        "stone/dal/tools/template/" + StringUtils.replace(name, "_" + locale.toString(), "");
                return Thread.currentThread().getContextClassLoader().getResource(urlName);
              }
            });
    cfg.setObjectWrapper(new DefaultObjectWrapper());
    try {
      Template temp = cfg.getTemplate("repo_java.ftl");
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      Writer out = new OutputStreamWriter(bos);
      SimpleHash params = new SimpleHash();
      params.put("className", entityMeta.getName()+"Repository");
      params.put("packageName", stone.dal.kernel.utils.StringUtils.replaceNull(packageName));
      params.put("doClass", entityMeta.getName());
      temp.process(params, out);
      out.flush();
      return new String(bos.toByteArray(), StandardCharsets.UTF_8);
    } catch (Exception e) {
      s_logger.error(e.getMessage());
      throw new Exception(e);
    }
  }


  private String genDoJavaClass(RawEntityMeta entityMeta, String packageName, Map<String, RawEntityMeta> mapper)
      throws Exception {
    List<RawFieldMeta> fields = entityMeta.getRawFields();
    List<String> pkFields = new ArrayList<>();
    for (FieldMeta field : fields) {
      if (ExcelUtils.booleanValueForBoolean(field.getPk())) {
        pkFields.add(field.getName());
      }
    }
    entityMeta.pks().clear();
    entityMeta.pks().addAll(pkFields);
    List<RawRelationMeta> relations = entityMeta.getRawRelations();
    if (!CollectionUtils.isEmpty(relations)) {
      for (RawRelationMeta relation : relations) {
        if (StringUtils.isEmpty(relation.getJoinPropertyTypeName())) {
          String propertyDomain = relation.getJoinDomain();
          relation.setJoinPropertyTypeName(packageName + "." + propertyDomain);
        }
      }
    }
    Configuration cfg = new Configuration();
    cfg.setTemplateLoader(
        new URLTemplateLoader() {
          protected URL getURL(String name) {
            Locale locale = Locale.getDefault();
            String urlName =
                "stone/dal/tools/template/" + StringUtils.replace(name, "_" + locale.toString(), "");
            return Thread.currentThread().getContextClassLoader().getResource(urlName);
          }
        });
    cfg.setObjectWrapper(new DefaultObjectWrapper());
    try {
      Template temp = cfg.getTemplate("entity_java.ftl");
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      Writer out = new OutputStreamWriter(bos);
      SimpleHash params = new SimpleHash();
      params.put("entity", entityMeta);
      params.put("className", entityMeta.getName());
      params.put("packageName", stone.dal.kernel.utils.StringUtils.replaceNull(packageName));
      params.put("entityDict", mapper);
      params.put("gen", this);
      temp.process(params, out);
      out.flush();
      return new String(bos.toByteArray(), StandardCharsets.UTF_8);
    } catch (Exception e) {
      s_logger.error(e.getMessage());
      throw new Exception(e);
    }
  }

  public boolean nosql(RawEntityMeta entity) {
    return ExcelUtils.booleanValueForBoolean(entity.isNosql());
  }

  public boolean many2many(EntityMeta entity, RelationMeta relationMeta) {
    return relationMeta.getRelationType() == RelationTypes.MANY_2_MANY;
  }

  public String many2manyAnnotation(RawEntityMeta entityMeta, RawRelationMeta relation,
      Map<String, RawEntityMeta> entityDict) {
    String joinTable = relation.getJoinTable();
    HashSet<String> pks = entityMeta.pks();
    List<String> joinColumns = new ArrayList<>();
    for (String pk : pks) {
      String joinColumn =
          "@JoinColumn(name = \"" + entityMeta.getName().toLowerCase() + "_" + pk + "\", referencedColumnName = \"" +
              pk + "\")";
      joinColumns.add(joinColumn);
    }
    RawEntityMeta relatedEntity = entityDict.get(relation.getJoinDomain());
    HashSet<String> inversePks = relatedEntity.pks();
    List<String> inverseJoinColumns = new ArrayList<>();
    for (String pk : inversePks) {
      String joinColumn =
          "@JoinColumn(name = \"" + relatedEntity.getName().toLowerCase() + "_" + pk + "\", referencedColumnName = \"" +
              pk + "\")";
      inverseJoinColumns.add(joinColumn);
    }
    return "@javax.persistence.JoinTable(name = \"" + joinTable + "\", " +
        "joinColumns = {" + StringUtils.join(joinColumns, ",") + "},\n" +
        "    inverseJoinColumns = {" + StringUtils.join(inverseJoinColumns, ",") + "})";
  }

  public String getFieldType(RawEntityMeta meta, RawFieldMeta fieldMeta) {
    String classType = fieldMeta.getTypeName();
    if (ExcelUtils.booleanValueForBoolean(meta.isNosql())) {
      if (classType.equalsIgnoreCase("bigDecimal")) {
        classType = "double";
      }
    }
    if ("file".equalsIgnoreCase(fieldMeta.getTypeName())
        || "clob".equalsIgnoreCase(fieldMeta.getTypeName())) {
      classType = "string";
    }
    return classTypeMap.get(classType).getName();
  }

  public boolean one2many(RawEntityMeta entity, RawRelationMeta relationMeta) {
    return relationMeta.getRelationType().name().equalsIgnoreCase("one_2_many");
  }

  public String getAnnotation(RawFieldMeta fieldMeta) {
    List<String> annotations = new ArrayList<>();
    if (ExcelUtils.booleanValueForBoolean(fieldMeta.getPk())) {
      annotations.add("@javax.persistence.Id");
    }
    if (ExcelUtils.booleanValueForBoolean(fieldMeta.getClob())) {
      annotations.add("@stone.dal.common.models.annotation.Clob");
    }
    if (!StringUtils.isEmpty(fieldMeta.getMappedBy()) && !StringUtils.isEmpty(fieldMeta.getMapper())) {
      annotations.add(
          "@FieldMapper(mapper = \"" + fieldMeta.getMapper() + "\", mappedBy = \"" + fieldMeta.getMappedBy() + "\")");
    }
    if (!boolValue(fieldMeta.getNotPersist())) {
      annotations.add("@Column(" + getColumnAnnotation(fieldMeta) + ")");
    }
    if (!StringUtils.isEmpty(fieldMeta.getSeqType())) {
      String annotation = "@Sequence(";
      if (!StringUtils.isEmpty(fieldMeta.getSeqKey())) {
        annotation += "key = \"" + fieldMeta.getSeqKey() + "\"";
      }
      if (!StringUtils.isEmpty(fieldMeta.getSeqType())) {
        annotation += "generator = \"" + fieldMeta.getSeqType() + "\"";
      }
      annotation += ")";
      annotations.add(annotation);
    }
    return ExcelUtils.combineString(annotations, "\n");
  }

  private String getColumnAnnotation(RawFieldMeta dataFieldMeta) {
    String annotation = "name=\"" + dataFieldMeta.getDbName() + "\"";
    String typeName = dataFieldMeta.getTypeName();
    if ("clob".equalsIgnoreCase(typeName) || "file".equalsIgnoreCase(typeName)) {
      typeName = "string";
    }
    Class type = classTypeMap.get(typeName);
    if (BigDecimal.class.getName().equals(type.getName())) {
      annotation += ", precision=" + dataFieldMeta.getPrecision() + ",scale=" + dataFieldMeta.getScale();
    } else if (Long.class.getName().equals(type.getName())) {
      annotation += ", precision=" + dataFieldMeta.getPrecision() + ",scale=0";
    } else if (Integer.class.getName().equals(type.getName())) {
      annotation += ", precision=" + dataFieldMeta.getPrecision() + ",scale=0";
    } else if (String.class.getName().equals(type.getName())) {
      if ("time".equals(dataFieldMeta.getTypeName())) {
        dataFieldMeta.setMaxLength(5);
      }
      if (dataFieldMeta.getMaxLength() != null) {
        annotation += ", length=" + dataFieldMeta.getMaxLength();

      }
    }
    if ("string".equalsIgnoreCase(typeName)) {
      if (dataFieldMeta.getMaxLength() == null) {
        annotation += ", length=100";
      }
    }
    if (!boolValue(dataFieldMeta.getNullable())) {
      annotation += ", nullable=false";
    }
    return annotation;
  }

  public String getMethodName(String fieldName) {
    return ExcelUtils.convertFirstAlphetUpperCase(fieldName);
  }

  public List<String> getPks(RawEntityMeta entityMeta) {
    return new ArrayList<>(entityMeta.pks());
  }

  private RawFieldMeta readField(String entityName, Row sfRow) throws Exception {
    RawFieldMeta fieldMeta = readFromExcel(sfRow);
    if ("file".equalsIgnoreCase(fieldMeta.getTypeName())) {
      fieldMeta.setFile(true);
    }
    String property = fieldMeta.getFieldProperty();
    if (!isStrEmpty(property)) {
      if (fieldMeta.getTypeName().equalsIgnoreCase("double")
          || fieldMeta.getTypeName().equalsIgnoreCase("long")
          || fieldMeta.getTypeName().equalsIgnoreCase("int")) {
        if (property.endsWith(".0")) {
          property = StringUtils.replace(property, ".0", "");
        }
        String[] fieldInfo = str2Arr(property, ",");
        fieldMeta.setPrecision(new Integer(fieldInfo[0]));
        if (fieldInfo.length > 1) {
          fieldMeta.setScale(new Integer(fieldInfo[1]));
        }
      } else {
        fieldMeta.setMaxLength(new Integer(str2Arr(property, ".")[0]));
      }
    }
    String seq = fieldMeta.getSeqDesc();
    if (!isStrEmpty(seq)) {
      if (seq.contains(":")) {
        fieldMeta.setSeqType(str2Arr(seq, ":")[0]);
        fieldMeta.setSeqStartNum(Integer.parseInt(str2Arr(seq, ":")[1]));
      } else {
        fieldMeta.setSeqType(seq);
      }
    } else if (fieldMeta.getPk()) {
      if ("string".equalsIgnoreCase(fieldMeta.getTypeName())) {
        fieldMeta.setSeqType("uuid");
      } else {
        fieldMeta.setSeqType("sequence");
      }
    }
    String unique = fieldMeta.getUnique();
    if (!isStrEmpty(unique)) {
      if (unique.equalsIgnoreCase("Y")) {
        fieldMeta.setUnique(entityName);
      } else {
        fieldMeta.setUnique(unique);
      }
    }
    String dbName = fieldMeta.getDbName();
    if (!isStrEmpty(dbName)) {
      fieldMeta.setDbName(dbName);
    } else {
      fieldMeta.setDbName(stone.dal.kernel.utils.StringUtils.canonicalPropertyName2DBField(fieldMeta.getName()));
    }
    if ("string".equals(fieldMeta.getTypeName())) {
      if (fieldMeta.getMaxLength() == null) {
        fieldMeta.setMaxLength(150);
      }
    }
    String mapperDesc = fieldMeta.getMapperDesc();
    if (!StringUtils.isEmpty(mapperDesc)) {
      if (mapperDesc.contains(":")) {
        fieldMeta.setMapper(str2Arr(seq, ":")[0]);
        fieldMeta.setMapperBy(str2Arr(seq, ":")[1]);
      }
    }
    validFieldMeta(entityName, fieldMeta);
    return fieldMeta;
  }

  private RawFieldMeta readFromExcel(Row sfRow) throws InvocationTargetException, IllegalAccessException {
    RawFieldMeta fieldMeta = new RawFieldMeta();
    ExcelSpecColumnEnu[] enus = ExcelSpecColumnEnu.values();
    for (ExcelSpecColumnEnu enu : enus) {
      String field = enu.name();
      Class propType = ClassUtils.getPropertyType(RawFieldMeta.class, field);
      Object cellVal = null;
      if (propType == String.class) {
        cellVal = ExcelUtils.cellStr(sfRow.getCell(enu.ordinal())).trim();
      } else if (propType == Boolean.class) {
        cellVal = ExcelUtils.cellBool(sfRow.getCell(enu.ordinal()));
      }
      Objects.requireNonNull(ClassUtils.getWriteMethod(RawFieldMeta.class, field)).invoke(fieldMeta, cellVal);
    }
    return fieldMeta;
  }

  private RawRelationMeta readRelations(RawEntityMeta entityMeta, Row sfRow, HashSet<String> n2nJoinTables) {
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
    if (!"file".equalsIgnoreCase(meta.getTypeName())
        && !"clob".equalsIgnoreCase(meta.getTypeName())) {
      if (!classTypeMap.containsKey(meta.getTypeName())) {
        throw new Exception("Entity:" + entityName + " Field:" + meta.getName() + "'type is invalid!\n" +
            "Please use one of the following type" +
            "string,date,datetime,int,long,double,boolean,time");
      }
    }
  }
}
