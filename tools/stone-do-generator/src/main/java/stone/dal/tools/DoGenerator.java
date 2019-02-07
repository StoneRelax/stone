package stone.dal.tools;

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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import stone.dal.common.models.meta.FieldMeta;
import stone.dal.common.models.meta.IndexMeta;
import stone.dal.common.models.meta.RelationMeta;
import stone.dal.common.models.meta.RelationTypes;
import stone.dal.kernel.utils.ClassUtils;
import stone.dal.kernel.utils.FileUtils;
import stone.dal.tools.ExtensionRuleReader.RuleSet;
import stone.dal.tools.meta.ExcelSpecColumnEnu;
import stone.dal.tools.meta.RawEntityMeta;
import stone.dal.tools.meta.RawFieldMeta;
import stone.dal.tools.meta.RawRelationMeta;
import stone.dal.tools.utils.DoGeneratorUtils;

import static stone.dal.kernel.utils.KernelUtils.boolValue;
import static stone.dal.kernel.utils.KernelUtils.isCollectionEmpty;
import static stone.dal.kernel.utils.KernelUtils.isStrEmpty;
import static stone.dal.kernel.utils.KernelUtils.replace;
import static stone.dal.kernel.utils.KernelUtils.replaceNull;
import static stone.dal.kernel.utils.KernelUtils.str2Arr;

public class DoGenerator {
  private static Map<String, Class> classTypeMap = new ConcurrentHashMap<>();

  private static Map<String, String> pkTypemap = new ConcurrentHashMap<>();

  private static Logger s_logger = LoggerFactory.getLogger(DoGenerator.class);

  static {
    pkTypemap.put("long", "Long");
    pkTypemap.put("string", "String");
    pkTypemap.put("int", "Integer");
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

  public void build(String xlsxPath, String targetSource,
      String rootPackage, String baseApiPath, String extensionRulePath) throws Exception {
    xlsxPath = StringUtils.isEmpty(xlsxPath) ? "entities.xlsx" : xlsxPath;
    String rootPath = targetSource != null ? targetSource : "gen-src";
    String jpaPackage = rootPackage == null ? "stone.dal.jpa" : rootPackage + ".jpa";
    String repoPackage = rootPackage == null ? "stone.dal.repo" : rootPackage + ".repo";
    String controllerPackage = rootPackage == null ? "stone.dal.controller" : rootPackage + ".controller";
    File sourceExcelFile = new File(xlsxPath);
    List<RawEntityMeta> entityMetas = parseFile(jpaPackage, sourceExcelFile);
    Map<String, ExtensionRuleReader.RuleSet.Rule> turnOnMap = ExtensionRuleReader.read(extensionRulePath)
        .getTurnOnMap();
    Set<String> hdrEntities = getHdrEntityNames(entityMetas);
    baseApiPath = baseApiPath == null ? "/api" : baseApiPath;
    writeDoFiles(entityMetas, rootPath, jpaPackage, turnOnMap, hdrEntities);
    writeRepositoryFiles(entityMetas, rootPath, repoPackage, jpaPackage);
    writeControllerFiles(entityMetas, baseApiPath, rootPath, controllerPackage, jpaPackage, repoPackage);
  }

  private Set<String> getHdrEntityNames(List<RawEntityMeta> entityMetas) {
    Map<String, RawEntityMeta> mapper = entityMetas.stream()
        .collect(Collectors.toMap(RawEntityMeta::getName, entity -> entity));
    Set<String> hdrEntities = new HashSet<>();
    entityMetas.forEach(entityMeta -> {
      List<RawRelationMeta> relations = entityMeta.getRawRelations();
      long count = relations.stream()
          .filter(rawRelationMeta -> rawRelationMeta.getRelationType() == RelationTypes.MANY_2_ONE).count();
      if (count == 0) {
        hdrEntities.add(entityMeta.getName());
      }
    });
    return hdrEntities;
  }

  private void writeControllerFiles(List<RawEntityMeta> entityMetas, String basePath, String rootPath,
      String packageName,
      String doPackage, String repoPackage) throws Exception {
    List<String> contents = createControllerJavaSource(entityMetas, basePath, packageName, doPackage, repoPackage);
    String javaFile;
    for (int i = 0; i < contents.size(); i++) {
      String content = contents.get(i);
      javaFile = rootPath + "/main/java/" + replace(packageName, ".", "/")
          + "/" + DoGeneratorUtils.convertFirstAlphetUpperCase(entityMetas.get(i).getName()) + "Controller" + ".java";
      if (!FileUtils.isExisted(javaFile)) {
        DoGeneratorUtils.writeFile(javaFile, content.getBytes());
      }
    }
  }

  private void writeRepositoryFiles(List<RawEntityMeta> entityMetas, String rootPath, String packageName,
      String jpaPackageName) throws Exception {
    List<String> contents = createRepoJavaSource(entityMetas, packageName, jpaPackageName);
    String javaFile;
    for (int i = 0; i < contents.size(); i++) {
      String content = contents.get(i);
      javaFile = rootPath + "/main/java/" + replace(packageName, ".", "/")
          + "/" + DoGeneratorUtils.convertFirstAlphetUpperCase(entityMetas.get(i).getName()) + "Repository" + ".java";
      if (!FileUtils.isExisted(javaFile)) {
        DoGeneratorUtils.writeFile(javaFile, content.getBytes());
      }
    }
  }

  private void writeDoFiles(List<RawEntityMeta> entityMetas, String rootPath, String packageName,
      Map<String, ExtensionRuleReader.RuleSet.Rule> turnOnMap, Set<String> hdrEntities)
      throws Exception {
    List<String> contents = createDoJavaFiles(entityMetas, packageName, turnOnMap, hdrEntities);
    String javaFile;
    for (int i = 0; i < contents.size(); i++) {
      String content = contents.get(i);
      javaFile = rootPath + "/main/java/" + replace(packageName, ".", "/")
          + "/" + DoGeneratorUtils.convertFirstAlphetUpperCase(entityMetas.get(i).getName()) + ".java";
      DoGeneratorUtils.writeFile(javaFile, content.getBytes());
    }
  }

  public boolean hasIndex(RawEntityMeta entityMeta) {
    return CollectionUtils.isNotEmpty(entityMeta.getRawIndicies());
  }

  public String isUnique(RawEntityMeta entityMeta, String idxName) {
    Optional<IndexMeta> optional = entityMeta.getRawIndicies().stream()
        .filter(index -> idxName.equals(index.getName())).findFirst();
    return optional.map(indexMeta -> String.valueOf(indexMeta.isUnique())).orElse("false");
  }

  public Set<String> indicies(RawEntityMeta entityMeta) {
    return entityMeta.getRawIndicies().stream().map(IndexMeta::getName).collect(Collectors.toSet());
  }

  public String[] getIndexColumns(RawEntityMeta entityMeta, String idxName) {
    Optional<IndexMeta> optional = entityMeta.getRawIndicies().stream()
        .filter(index -> idxName.equals(index.getName())).findFirst();
    return optional.map(IndexMeta::getColumnNames).orElse(null);
  }

  public String dbIdxName(RawEntityMeta entityMeta, String idxName) {
    Optional<IndexMeta> optional = entityMeta.getRawIndicies().stream()
        .filter(index -> idxName.equals(index.getName())).findFirst();
    return optional.map(indexMeta -> {
      if (indexMeta.isUnique()) {
        return ("UNIQUE_" + idxName).toUpperCase();
      }
      return ("idx_" + idxName).toUpperCase();
    }).orElse(null);
  }

  private List<RawEntityMeta> parseFile(String packageName, File file) throws Exception {
    InputStream is = new FileInputStream(file);
    Workbook book = DoGeneratorUtils.getWorkbook(is);
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
      meta.setClazzName(packageName + "." + entityName);
      String tableName = stone.dal.kernel.utils.StringUtils.canonicalPropertyName2DBField(entityName);
      meta.setTableName(tableName);
      meta.setNosql(DoGeneratorUtils.cellBool(headRow.getCell(3)));
      String delInd = DoGeneratorUtils.cellStr(headRow.getCell(9));
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
          if (beginRelationRead && (cell == null || isStrEmpty(DoGeneratorUtils.cellStr(cell)))) {
            break;
          }
          String sv = DoGeneratorUtils.cellStr(cell);
          if (sv.equals("<<Fields>>")) {
            beginFieldRead = true;
          } else if (sv.equals("<<Relations>>")) {
            beginFieldRead = false;
            beginRelationRead = true;
          } else if (!isStrEmpty(sv)) {
            if (beginRelationRead && !replaceNull(DoGeneratorUtils.cellStr(sfRow.getCell(0))).equals("Type")) {
              RawRelationMeta relation = readRelations(meta, sfRow, n2nJoinTables);
              meta.getRawRelations().add(relation);
            } else if (beginFieldRead && !replaceNull(DoGeneratorUtils.cellStr(sfRow.getCell(0))).equals("Name")) {
              RawFieldMeta fieldMeta = readField(entityName, sfRow);
              meta.getRawFields().add(fieldMeta);
            }
          }
        } else {
          if (beginRelationRead) {
            break;
          }
          if (row > 1000) {
            break;
          }
        }
        row++;
      }
      resolveFileFields(meta);
      resolveColumnMapperAssociateColumns(meta);
      buildIndices(meta, true);
      buildIndices(meta, false);
      entities.add(meta);
      i++;
    }
    resolveReverseRelation(entities);
    return entities;
  }

  private void resolveReverseRelation(List<RawEntityMeta> entities) {
    Map<String, RawEntityMeta> map = entities.stream()
        .collect(Collectors.toMap(RawEntityMeta::getName, entity -> entity));
    entities.forEach(entity -> {
      List<RawRelationMeta> relations = entity.getRawRelations();
      if (!isCollectionEmpty(relations)) {
        for (RawRelationMeta relation : relations) {
          if (RelationTypes.ONE_2_MANY.equals(relation.getRelationType())) {
            RawEntityMeta entityMeta = map.get(relation.getJoinDomain());
            Assert.notNull(entityMeta, String.format("Cant find related object %s", relation.getJoinDomain()));
            RawRelationMeta many2one = new RawRelationMeta();
            many2one.setRelationType(RelationTypes.MANY_2_ONE);
            many2one.setRefColumn((entity.pks().iterator().next()).toLowerCase()); //todo: fix if pk fields have 2 more.
            many2one.setUpdatable(false);
            many2one.setNullable(true);
            many2one.setJoinColumnName(
                (entity.getName().toLowerCase() + "_" + entity.pks().iterator().next()).toLowerCase());
            many2one.setJoinProperty(DoGeneratorUtils.convertFirstAlphetLowerCase(entity.getName()));
            many2one.setJoinDomain(entity.getName());
            entityMeta.getRawRelations().add(many2one);
          } else if (RelationTypes.ONE_2_ONE_REF.equals(relation.getRelationType())
              || RelationTypes.ONE_2_ONE_VAL.equals(relation.getRelationType())) {
//            if (str_emp(relation.getJoinColumnName())) {
//              List<RelationMeta> one2one = one2oneComplementary.get(relation.getJoinDomain());
//              if (one2one == null) {
//                one2one = new ArrayList<>();
//                one2oneComplementary.put(relation.getJoinDomain(), one2one);
//              }
//              one2one.add(relation);
//            }
            //todo: fix if pk fields have 2 more.
          }
        }
      }
    });
  }

  public boolean one2one(RelationMeta relationMeta) {
    return relationMeta.getRelationType() == RelationTypes.ONE_2_ONE_VAL
        || relationMeta.getRelationType() == RelationTypes.ONE_2_ONE_REF;
  }

  public boolean one2many(RelationMeta relationMeta) {
    return relationMeta.getRelationType() == RelationTypes.ONE_2_MANY;
  }

  public boolean many2many(RelationMeta relationMeta) {
    return relationMeta.getRelationType() == RelationTypes.MANY_2_MANY;
  }

  private void resolveFileFields(RawEntityMeta meta) {
    List<RawFieldMeta> fileUuidFields = meta.getRawFields().stream().filter(FieldMeta::getFile).map(
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

  private void buildIndices(RawEntityMeta meta, boolean unique) {
    Map<String, List<String>> indexMap = new HashMap<>();
    meta.getRawFields().forEach(fieldMeta -> {
      String idxName = unique ? fieldMeta.getUnique() : fieldMeta.getIndex();
      if (StringUtils.isNotEmpty(idxName)) {
        if (indexMap.containsKey(idxName)) {
          indexMap.get(idxName).add(fieldMeta.getDbName());
        } else {
          List<String> columns = new ArrayList<>();
          columns.add(fieldMeta.getDbName());
          indexMap.put(idxName, columns);
        }
      }
    });
    indexMap.forEach((index, columns) -> {
      meta.getRawIndicies().add(new IndexMeta(columns.toArray(new String[0]), index, unique));
    });
  }

  public boolean hasEntityListenerListener(RawEntityMeta entityMeta) {
    return !CollectionUtils.isEmpty(entityMeta.getEntityListeners());
  }

  public boolean hasListenerIntf(RawEntityMeta entityMeta) {
    return !CollectionUtils.isEmpty(entityMeta.getEntityListeners().stream()
        .filter(entityListener -> !StringUtils.isEmpty(entityListener.getInterfaceName())).collect(
            Collectors.toList()));
  }

  public List<String> createDoJavaFiles(List<RawEntityMeta> entities,
      String packageName, Map<String, ExtensionRuleReader.RuleSet.Rule> turnOnMap, Set<String> hdrEntities)
      throws Exception {
    Map<String, RawEntityMeta> mapper = entities.stream()
        .collect(Collectors.toMap(RawEntityMeta::getName, entityMeta -> entityMeta));
    List<String> javaContents = new ArrayList<>();
    for (RawEntityMeta entityMeta : entities) {
      List<RawFieldMeta> fields = entityMeta.getRawFields();
      List<String> pkFields = new ArrayList<>();
      for (FieldMeta field : fields) {
        if (DoGeneratorUtils.booleanValueForBoolean(field.getPk())) {
          pkFields.add(field.getName());
        }
      }
      addExtensionFields(entityMeta, turnOnMap, hdrEntities, entities);
      addEntityListeners(entityMeta, turnOnMap, hdrEntities, entities);
      entityMeta.pks().clear(); //todo: why clear?
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
      javaContents.add(new String(bos.toByteArray(), StandardCharsets.UTF_8));
    }
    return javaContents;
  }

  private void resolveColumnMapperAssociateColumns(RawEntityMeta entityMeta) {
    //todo: consider if fields exist
    List<RawFieldMeta> addOnFields = entityMeta.getRawFields().stream()
        .filter(field -> field.getColumnMapperDslMeta() != null).map(fieldMeta -> {
          RawFieldMeta rawFieldMeta = new RawFieldMeta();
          String associateColumn = fieldMeta.getColumnMapperDslMeta().getAssociateColumn();
          rawFieldMeta.setName(fieldMeta.getColumnMapperDslMeta().getAssociateColumn());
          rawFieldMeta.setTypeName(fieldMeta.getColumnMapperDslMeta().getAssociateColumnType());
          String dbName = stone.dal.kernel.utils.StringUtils.canonicalPropertyName2DBField(associateColumn);
          rawFieldMeta.setDbName(dbName);
          resolveDefaultFieldProperty(rawFieldMeta);
          return rawFieldMeta;
        }).collect(Collectors.toList());
    entityMeta.getRawFields().addAll(addOnFields);
  }

  private void addEntityListeners(RawEntityMeta entityMeta, Map<String, RuleSet.Rule> turnOnMap,
      Set<String> hdrEntities, List<RawEntityMeta> allEntities) {
    RuleSet.Rule bothRuleSet = turnOnMap.get(ExtensionRuleReader.TurnOnSwitches.both.name());
    if (bothRuleSet != null) {
      this.addEntityListener(entityMeta, bothRuleSet);
    }
    if (hdrEntities.contains(entityMeta.getName())) {
      ExtensionRuleReader.RuleSet.Rule headerRuleSet = turnOnMap.get(ExtensionRuleReader.TurnOnSwitches.header.name());
      if (headerRuleSet != null) {
        this.addEntityListener(entityMeta, headerRuleSet);
      }
    } else {
      ExtensionRuleReader.RuleSet.Rule dtlRuleSet = turnOnMap.get(ExtensionRuleReader.TurnOnSwitches.details.name());
      this.addEntityListener(entityMeta, dtlRuleSet);
    }
  }

  private void addExtensionFields(RawEntityMeta entityMeta, Map<String, RuleSet.Rule> turnOnMap,
      Set<String> hdrEntities, List<RawEntityMeta> allEntities) {
    RuleSet.Rule bothRuleSet = turnOnMap.get(ExtensionRuleReader.TurnOnSwitches.both.name());
    if (bothRuleSet != null) {
      this.addExtFields(entityMeta, bothRuleSet);
    }
    if (hdrEntities.contains(entityMeta.getName())) {
      ExtensionRuleReader.RuleSet.Rule headerRuleSet = turnOnMap.get(ExtensionRuleReader.TurnOnSwitches.header.name());
      if (headerRuleSet != null) {
        this.addExtFields(entityMeta, headerRuleSet);
      }
    } else {
      ExtensionRuleReader.RuleSet.Rule dtlRuleSet = turnOnMap.get(ExtensionRuleReader.TurnOnSwitches.details.name());
      this.addExtFields(entityMeta, dtlRuleSet);
    }
  }

  private void addExtFields(RawEntityMeta entityMeta, RuleSet.Rule ruleSet) {
    List<RawFieldMeta> rawFieldMetas = entityMeta.getRawFields();
    rawFieldMetas.addAll(ruleSet.getAddOnFields());
    entityMeta.setRawFields(rawFieldMetas);
  }

  private void addEntityListener(RawEntityMeta entityMeta, RuleSet.Rule ruleSet) {
    entityMeta.setEntityListeners(ruleSet.getEntityListeners());
  }

  public List<String> createRepoJavaSource(List<RawEntityMeta> entities, String packageName, String jpaPackageName)
      throws Exception {
    List<String> javaContents = new ArrayList<>();
    for (RawEntityMeta entityMeta : entities) {
      String javaContent = genRepoJavaClass(entityMeta, packageName, jpaPackageName);
      javaContents.add(javaContent);
    }
    return javaContents;
  }

  public List<String> createControllerJavaSource(List<RawEntityMeta> entities,
      String basePath, String packageName, String doPackage, String repoPackage) throws Exception {
    List<String> javaContents = new ArrayList<>();
    for (RawEntityMeta entityMeta : entities) {
      String javaContent = genControllerJavaClass(entityMeta,
          basePath, packageName, doPackage, repoPackage);
      javaContents.add(javaContent);
    }
    return javaContents;
  }

  private String genControllerJavaClass(RawEntityMeta entityMeta,
      String basePath,
      String packageName, String doPackage, String repoPackage) throws Exception {
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
      RawFieldMeta pkField = getPKField(entityMeta);
      String pkType = pkField.getTypeName();
      Template temp = cfg.getTemplate("controller_java.ftl");
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      Writer out = new OutputStreamWriter(bos);
      SimpleHash params = new SimpleHash();
      params.put("className", entityMeta.getName() + "Controller");
      params.put("packageName", stone.dal.kernel.utils.StringUtils.replaceNull(packageName));
      params.put("repoName", entityMeta.getName() + "Repository");
      params.put("basePath", basePath);
      params.put("doPackage", doPackage);
      params.put("repoPackage", repoPackage);
      params.put("doName", entityMeta.getName());
      String tableName = stone.dal.kernel.utils.StringUtils.canonicalPropertyName2DBField(entityMeta.getName());
      params.put("lowerDoName", StringUtils.replace(tableName, "_", "-").toLowerCase());
      params.put("pkType", pkTypemap.get(pkType));
      params.put("pkName", upperCase(pkField.getName()));
      temp.process(params, out);
      out.flush();
      return new String(bos.toByteArray(), StandardCharsets.UTF_8);
    } catch (Exception e) {
      s_logger.error(e.getMessage());
      throw new Exception(e);
    }
  }

  private String genRepoJavaClass(RawEntityMeta entityMeta, String packageName, String jpaPackageName)
      throws Exception {
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
      RawFieldMeta pkField = getPKField(entityMeta);
      String pkType = pkField.getTypeName();
      Template temp = cfg.getTemplate("repo_java.ftl");
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      Writer out = new OutputStreamWriter(bos);
      SimpleHash params = new SimpleHash();
      params.put("className", entityMeta.getName() + "Repository");
      params.put("packageName", stone.dal.kernel.utils.StringUtils.replaceNull(packageName));
      params.put("jpaPackageName", jpaPackageName);
      params.put("doName", entityMeta.getName());
      params.put("pkType", pkTypemap.get(pkType));
      params.put("pkName", upperCase(pkField.getName()));
      temp.process(params, out);
      out.flush();
      return new String(bos.toByteArray(), StandardCharsets.UTF_8);
    } catch (Exception e) {
      s_logger.error(e.getMessage());
      throw new Exception(e);
    }
  }

  public String upperCase(String str) {
    char[] ch = str.toCharArray();
    if (ch[0] >= 'a' && ch[0] <= 'z') {
      ch[0] = (char) (ch[0] - 32);
    }
    return new String(ch);
  }

  private RawFieldMeta getPKField(RawEntityMeta entityMeta) {
    RawFieldMeta pkField = null;
    List<RawFieldMeta> fields = entityMeta.getRawFields();
    for (RawFieldMeta fieldMeta : fields) {
      if (fieldMeta.getPk()) {
        pkField = fieldMeta;
        break;
      }
    }
    return pkField;
  }

  public boolean nosql(RawEntityMeta entity) {
    return DoGeneratorUtils.booleanValueForBoolean(entity.isNosql());
  }

  public String many2manyAnnotation(RawEntityMeta entityMeta, RawRelationMeta relation,
      Map<String, RawEntityMeta> entityDict) {
    String joinTable = relation.getJoinTable();
    HashSet<String> pks = entityMeta.pks();
    List<String> joinColumns = new ArrayList<>();
    for (String pk : pks) {
      String joinColumn =
          "@javax.persistence.JoinColumn(name = \"" + entityMeta.getName().toLowerCase() + "_" + pk +
              "\", referencedColumnName = \"" +
              pk + "\")";
      joinColumns.add(joinColumn);
    }
    RawEntityMeta relatedEntity = entityDict.get(relation.getJoinDomain());
    HashSet<String> inversePks = relatedEntity.pks();
    List<String> inverseJoinColumns = new ArrayList<>();
    for (String pk : inversePks) {
      String joinColumn =
          "@javax.persistence.JoinColumn(name = \"" + relatedEntity.getName().toLowerCase() + "_" + pk +
              "\", referencedColumnName = \"" +
              pk + "\")";
      inverseJoinColumns.add(joinColumn);
    }
    return "@javax.persistence.JoinTable(name = \"" + joinTable + "\", " +
        "joinColumns = {" + StringUtils.join(joinColumns, ",") + "},\n" +
        "    inverseJoinColumns = {" + StringUtils.join(inverseJoinColumns, ",") + "})";
  }

  public String getFieldType(RawEntityMeta meta, RawFieldMeta fieldMeta) {
    String classType = fieldMeta.getTypeName();
    if (DoGeneratorUtils.booleanValueForBoolean(meta.isNosql())) {
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

  public boolean isFieldPersist(RawFieldMeta fieldMeta) {
    return fieldMeta.getNotPersist() || fieldMeta.getColumnMapperDslMeta() != null;
  }

  public String getAnnotation(RawFieldMeta fieldMeta) {
    List<String> annotations = new ArrayList<>();
    if (DoGeneratorUtils.booleanValueForBoolean(fieldMeta.getPk())) {
      annotations.add("@javax.persistence.Id");
    }
    if (DoGeneratorUtils.booleanValueForBoolean(fieldMeta.getClob())) {
      annotations.add("@stone.dal.common.models.annotation.Clob");
    }
    boolean notPersist = isFieldPersist(fieldMeta);
    if (notPersist) {
      annotations.add("@javax.persistence.Transient");
    } else {
      annotations.add("@javax.persistence.Column(" + getColumnAnnotation(fieldMeta) + ")");
    }
    if (fieldMeta.getColumnMapperDslMeta() != null) {
      RawFieldMeta.ColumnMapperDslMeta columnMapperDslMeta = fieldMeta.getColumnMapperDslMeta();
      annotations.add(
          "@stone.dal.common.models.annotation.ColumnMapper(mapper = " + columnMapperDslMeta.getMapper() + ".class, " +
              "associateColumn = \"" + columnMapperDslMeta.getAssociateColumn() + "\", args = \"" +
              columnMapperDslMeta.getArgs() + "\")");
    }
    if (!StringUtils.isEmpty(fieldMeta.getSeqType())) {
      String annotation = "@stone.dal.common.models.annotation.Sequence(";
      if (!StringUtils.isEmpty(fieldMeta.getSeqKey())) {
        annotation += "key = \"" + fieldMeta.getSeqKey() + "\"";
      }
      if (!StringUtils.isEmpty(fieldMeta.getSeqType())) {
        annotation += "generator = \"" + fieldMeta.getSeqType() + "\"";
      }
      if (fieldMeta.getSeqStartNum() > 0) {
        annotation += ", defaultStartSeq = " + fieldMeta.getSeqStartNum();
      }
      annotation += ")";
      annotations.add(annotation);
    }
    return DoGeneratorUtils.combineString(annotations, "\n");
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
      if (dataFieldMeta.getMaxLength() > 0) {
        annotation += ", length=" + dataFieldMeta.getMaxLength();
      }
    }
    if ("string".equalsIgnoreCase(typeName)) {
      if (dataFieldMeta.getMaxLength() == 0) {
        annotation += ", length=100";
      }
    }
    if (!boolValue(dataFieldMeta.getNullable())) {
      annotation += ", nullable=false";
    }
    return annotation;
  }

  public String getMethodName(String fieldName) {
    return DoGeneratorUtils.convertFirstAlphetUpperCase(fieldName);
  }

  public List<String> getPks(RawEntityMeta entityMeta) {
    return new ArrayList<>(entityMeta.pks());
  }

  private RawFieldMeta readField(String entityName, Row sfRow) throws Exception {
    RawFieldMeta fieldMeta = readFromExcel(sfRow);
    if ("file".equalsIgnoreCase(fieldMeta.getTypeName())) {
      fieldMeta.setFile(true);
      fieldMeta.setMaxLength(200);
    } else if ("clob".equalsIgnoreCase(fieldMeta.getTypeName())) {
      fieldMeta.setClob(true);
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
    } else {
      resolveDefaultFieldProperty(fieldMeta);
    }
    String seq = fieldMeta.getSeqDsl();
    if (!isStrEmpty(seq)) {
      if (seq.contains(":")) {
        fieldMeta.setSeqType(str2Arr(seq, ":")[0]);
        fieldMeta.setSeqStartNum(Integer.parseInt(str2Arr(seq, ":")[1]));
      } else {
        fieldMeta.setSeqType(seq);
        fieldMeta.setSeqStartNum(0);
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
    validFieldMeta(entityName, fieldMeta);
    return fieldMeta;
  }

  private void resolveDefaultFieldProperty(RawFieldMeta fieldMeta) {
    if (fieldMeta.getTypeName().equalsIgnoreCase("long")) {
      fieldMeta.setPrecision(18);
    } else if (fieldMeta.getTypeName().equalsIgnoreCase("int")) {
      fieldMeta.setPrecision(7);
    } else if (fieldMeta.getTypeName().equalsIgnoreCase("string")) {
      fieldMeta.setMaxLength(128);
    }
  }

  private RawFieldMeta readFromExcel(Row sfRow) throws InvocationTargetException, IllegalAccessException {
    RawFieldMeta fieldMeta = new RawFieldMeta();
    ExcelSpecColumnEnu[] enus = ExcelSpecColumnEnu.values();
    for (ExcelSpecColumnEnu enu : enus) {
      String field = enu.name();
      Class propType = ClassUtils.getPropertyType(RawFieldMeta.class, field);
      Object cellVal = null;
      if (propType == String.class) {
        cellVal = DoGeneratorUtils.cellStr(sfRow.getCell(enu.ordinal())).trim();
      } else if (propType == Boolean.class) {
        cellVal = DoGeneratorUtils.cellBool(sfRow.getCell(enu.ordinal()));
      }
      Objects.requireNonNull(ClassUtils.getWriteMethod(RawFieldMeta.class, field)).invoke(fieldMeta, cellVal);
    }
    return fieldMeta;
  }

  private RawRelationMeta readRelations(RawEntityMeta entityMeta, Row sfRow, HashSet<String> n2nJoinTables) {
    RawRelationMeta relation = new RawRelationMeta();
    relation.setJoinDomain(DoGeneratorUtils.cellStr(sfRow.getCell(1)));
    relation.setJoinProperty(DoGeneratorUtils.cellStr(sfRow.getCell(2)));
    relation.setJoinColumnName(DoGeneratorUtils.cellStr(sfRow.getCell(3)));
    String type = DoGeneratorUtils.cellStr(sfRow.getCell(0));
    if ("1:N".equals(type)) {
      type = RelationTypes.ONE_2_MANY.name();
      relation.setMappedBy(DoGeneratorUtils.convertFirstAlphetLowerCase(entityMeta.getName()));
    }
    if ("N:N".equals(type)) {
      String joinTable = DoGeneratorUtils.cellStr(sfRow.getCell(4));
      type = RelationTypes.MANY_2_MANY.name();
      if (isStrEmpty(joinTable)) {
        String reverseJoinTable = relation.getJoinDomain().toLowerCase() + "_" + entityMeta.getName().toLowerCase();
        if (!n2nJoinTables.contains(reverseJoinTable.toUpperCase())) {
          joinTable = entityMeta.getName().toLowerCase() + "_" + relation.getJoinDomain().toLowerCase();
        } else {
          joinTable = reverseJoinTable;
        }
      }
      relation.setJoinTable(joinTable.toUpperCase());
      n2nJoinTables.add(relation.getJoinTable());
    }
    if ("1:1".equals(type)) {
      type = RelationTypes.ONE_2_ONE_REF.name();
      relation.setUpdatable(false);
    }
    relation.setRelationType(RelationTypes.valueOf(type));
    return relation;
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
