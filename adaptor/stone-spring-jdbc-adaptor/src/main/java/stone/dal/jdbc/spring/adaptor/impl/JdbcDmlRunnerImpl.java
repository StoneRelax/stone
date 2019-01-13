package stone.dal.jdbc.spring.adaptor.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import stone.dal.jdbc.JdbcDmlRunner;
import stone.dal.jdbc.api.meta.SqlDmlDclMeta;
import stone.dal.jdbc.impl.RdbmsEntity;
import stone.dal.jdbc.impl.RdbmsEntityManager;
import stone.dal.kernel.utils.KernelUtils;
import stone.dal.models.EntityMetaManager;
import stone.dal.models.data.BaseDo;
import stone.dal.models.meta.EntityMeta;
import stone.dal.seq.spi.SequenceSpi;

import java.util.Set;

public class JdbcDmlRunnerImpl implements JdbcDmlRunner {
    private static Logger logger = LoggerFactory.getLogger(JdbcDmlRunner.class);


    private NamedParameterJdbcTemplate jdbcTemplate;
    private EntityMetaManager entityMetaManager;
    private RdbmsEntityManager entityManager;
    private SequenceSpi sequenceSpiAdapter;


    public JdbcDmlRunnerImpl(NamedParameterJdbcTemplate jdbcTemplate, EntityMetaManager entityMetaManager, RdbmsEntityManager entityManager, SequenceSpi sequenceSpiAdapter) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMetaManager = entityMetaManager;
        this.entityManager = entityManager;
        this.sequenceSpiAdapter = sequenceSpiAdapter;
    }

    @Override
    public int run(SqlDmlDclMeta meta) {
        logger.info("SQL:{}", meta.getSql().toUpperCase());
        logger.info("PARAMETERS:[{}]", KernelUtils.arr2Str(meta.getParameters().values().toArray(), ","));
        jdbcTemplate.update(meta.getSql(),meta.getParameters());
        return 0;
    }

    @Override
    public void runInsert(BaseDo obj) {
        EntityMeta meta = entityMetaManager.getEntity(obj.getClass());
        RdbmsEntity entity = entityManager.build(meta);
        bindSequenceValues(obj, entity);
        SqlDmlDclMeta sqlDmlDclMeta = entity.getInsertMeta(obj);
        run(sqlDmlDclMeta);
    }

    @Override
    public void runDelete(BaseDo obj) {
        EntityMeta meta = entityMetaManager.getEntity(obj.getClass());
        RdbmsEntity entity = entityManager.build(meta);
        SqlDmlDclMeta sqlDmlDclMeta = entity.getDeleteMeta(obj);
        run(sqlDmlDclMeta);
    }

    @Override
    public void runUpdate(BaseDo obj) {
        EntityMeta meta = entityMetaManager.getEntity(obj.getClass());
        RdbmsEntity entity = entityManager.build(meta);
        bindSequenceValues(obj, entity);
        SqlDmlDclMeta sqlDmlDclMeta = entity.getUpdateMeta(obj);
        run(sqlDmlDclMeta);
    }


    private void bindSequenceValues(BaseDo obj, RdbmsEntity entity) {

        if (sequenceSpiAdapter != null) {
            Set<String> seqFields = entity.getSeqFields();
            Set<String> seqGenerators = entity.getSeqGenerators();
            for (String seqField : seqFields) {
                Object v = KernelUtils.getPropVal(obj, seqField);
                if (v == null) {
                    try {
                        v = sequenceSpiAdapter.next(obj, seqField);
                        KernelUtils.setPropVal(obj, seqField, v);
                    }catch (Exception e){
                        // TODO handle exception
                    }

                }
            }
        }
    }
}
