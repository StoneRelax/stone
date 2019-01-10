package stone.dal.common;

import stone.dal.common.api.DalEntityMetaManager;
import stone.dal.common.api.DalQueryPostHandler;
import stone.dal.kernel.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Collection;

/**
 * @author fengxie
 */
public class DalMapperPostHandler implements DalQueryPostHandler {
	private DalEntityMetaManager dalEntityMetaManager;
	private ApplicationContext applicationContext;
	private static Logger tracer = LoggerFactory.getLogger(DalMapperPostHandler.class);

	public DalMapperPostHandler(DalEntityMetaManager dalEntityMetaManager, ApplicationContext applicationContext) {
		this.dalEntityMetaManager = dalEntityMetaManager;
		this.applicationContext = applicationContext;
	}

	@Override
	public void readRows(Collection rows) throws AppException {
	}


	public boolean readRow(Object rowObj) throws AppException {
//		EntityMeta meta = dalEntityMetaManager.getEntityByClass(rowObj.getClass());
//		Set<FieldMeta> fields = dalEntityMetaManager.getMapperFields(meta);
//		for (FieldMeta field : fields) {
//			String[] filterFields = str_2_arr(field.getMappedBy(), ",");
//			String mapperDesc = field.getMapper();
//			DalFieldMapper mapper;
//			if (mapperDesc.contains(":")) {
//				String[] mapInfo = str_2_arr(field.getMapper(), ":");
//				String mapperName = mapInfo[0];
//				mapper = (DalFieldMapper) applicationContext.getBean(mapperName);
////				if (mapInfo[0].equals("javascript")) {
//////					if (mapInfo.length < 2) {
//////						throw new PlatformRuntimeException(mapperName + " is invalid!" + "[" + field.getEntity() + "." + field.getName() + "]");
//////					}
//////					String jsApi = mapInfo[1];
//////					String[] jsApiInfo = str_2_arr(mapInfo[1], ".");
//////					if (jsApiInfo.length < 2) {
//////						throw new PlatformRuntimeException(mapperName + " is invalid!" + "[" + field.getEntity() + "." + field.getName() + "]");
//////					}
//////					String fieldName = field.getName();
//////					String apiObj = jsApiInfo[0];
//////					String apiName = jsApiInfo[1];
//////					ScriptContext script_ctx = new ScriptContext();
//////					script_ctx.setByName(true);
//////					ScriptProfile profile = new ScriptProfile();
//////					profile.setName(apiObj);
//////					script_ctx.setProfile(profile);
//////					script_ctx.setFunc(apiName);
//////					List<Object> args = new ArrayList<>();
//////					Map<String, Object> opts = new HashMap<>();
//////					opts.put("obj", rowObj);
//////					opts.put("field", fieldName);
//////					opts.put("mappedBy", filterFields[0]);
//////					args.add(opts);
//////					script_ctx.setArgs(args);
//////					Object v = scriptRunner.eval(script_ctx);
//////					if (v != null) {
//////						ClassUtilities.setPropertyValue(rowObj, fieldName, v);
//////					}
////				} else if (mapInfo[0].equals("i18n")) {
////					if (mapInfo.length < 2) {
////						throw new PlatformRuntimeException(mapperDesc + " is invalid!" + "[" + field.getEntity() + "." + field.getName() + "]");
////					}
////					String fieldName = field.getName();
////					if (!StringUtils.isEmpty(field.getAlias())) {
////						fieldName = field.getAlias();
////					}
////					String i18nObj = mapInfo[1];
////					EntityMeta entityMeta = dataDictionary.getEntityMeta(i18nObj);
////					if (bool_v(entityMeta.getLocalizationRequired())) {
////						I18nObjMapper i18nObjMapper = bean("i18nObjMapper");
////						String desc = i18nObjMapper.desc(entityMeta, rowObj, fieldName, filterFields);
////						if (desc != null) {
////							ClassUtilities.setPropertyValue(rowObj, fieldName, desc);
////						}
////					}
////				} else if (mapInfo[0].equals("entity")) {
////					if (mapInfo.length < 2) {
////						throw new PlatformRuntimeException(mapperDesc + " is invalid!" + "[" + field.getEntity() + "." + field.getName() + "]");
////					}
////					String fieldName = field.getName();
////					if (!StringUtils.isEmpty(field.getAlias())) {
////						fieldName = field.getAlias();
////					}
////					String[] mapInfo1 = str_2_arr(mapInfo[1], ".");
////					if (mapInfo1.length < 2) {
////						throw new PlatformRuntimeException(mapperDesc + " is invalid!" + "[" + field.getEntity() + "." + field.getName() + "]");
////					}
////					String entityName = mapInfo1[0];
////					String checkField = mapInfo1[1];
////					String cacheKey = null;
////					if (mapInfo1.length > 2) {
////						cacheKey = mapInfo1[2];
////					}
////					EntityMapper entityMapper = bean("entityMapper");
////					String desc = entityMapper.desc(entityName, rowObj, cacheKey, filterFields[0], checkField);
////					if (desc != null) {
////						ClassUtilities.setPropertyValue(rowObj, fieldName, desc);
////					}
////				}
//			} else {
//				mapper = (DalFieldMapper) applicationContext.getBean(mapperDesc);
//				String fieldName = field.getName();
//				if (str_emp(fieldName)) {
//					fieldName = field.getDbName();
//				}
//				if (mapper != null) {
//					Object description = mapper.getMapperVal(rowObj, fieldName);
//					if (description != null) {
//						BeanUtils.setPropertyValue(rowObj, fieldName, description);
//					}
//				} else {
//					tracer.error(mapperDesc + " is not found!");
//				}
//			}
//		}
		return true;
	}

}
