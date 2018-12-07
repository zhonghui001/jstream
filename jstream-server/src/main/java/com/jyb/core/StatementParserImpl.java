package com.jyb.core;

import com.jyb.config.*;
import com.jyb.exception.BeanCheckException;
import com.jyb.exception.LostConfException;
import com.jyb.exception.SqlParseException;
import com.jyb.exception.UnsupportSqlException;
import com.jyb.parser.antlr.AntlrSqlParser;
import com.jyb.parser.antlr.tree.*;
import com.jyb.sink.AbstractSinkConfig;
import com.jyb.source.KafkaSource;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.reflections.ReflectionUtils.getAllFields;
import static org.reflections.ReflectionUtils.withAnnotation;

import static com.jyb.exception.util.ExecptionUtil.*;

public class StatementParserImpl implements StatementParser {

    private AntlrSqlParser parser;
    private Map<String, Class> souceSinkMapConfig = new HashMap<String, Class>();

    //sql 中source sink默认参数名，用于判断其类型
    private static final String TYPE = "type";


    @Inject
    public StatementParserImpl(AntlrSqlParser parser) {
        this.parser = requireNonNull(parser);
        init();
    }

    /**
     * 扫描Config接口实现类 填充souceSinkMapConfig
     */
    private void init() {
        Reflections reflections = new Reflections("com.jyb", new SubTypesScanner(true));
        Set<Class<? extends Config>> subTypes = reflections.getSubTypesOf(Config.class);
        subTypes.stream().forEach(cl -> {
            Arrays.stream(cl.getAnnotations())
                    .filter(name -> name instanceof Name)
                    .map(name -> (Name) name)
                    .filter(name -> StringUtils.isNotEmpty(name.value()))
                    .forEach(name -> {
                        souceSinkMapConfig.put(StringUtils.upperCase(name.value()), cl);
                    });
        });
    }

    @Override
    public JstreamConfiguration parser(String sql) {
        String sqlSplits[] = sql.split(";");
        JstreamConfiguration jstreamConfiguration = new JstreamConfiguration();
        for (String sqlSplit : sqlSplits) {
            if (sqlSplit.trim().startsWith("--") || StringUtils.isBlank(sqlSplit.replace("\r\n","").trim()))
                continue;
            Statement statement = parser.createStatement(sqlSplit);
            try {
                fillConfiguration(statement, jstreamConfiguration);
            } catch (SqlParseException ex) {
                throw ex;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        checkJstreamConfiguration(jstreamConfiguration);
        return jstreamConfiguration;
    }

    private void checkJstreamConfiguration(JstreamConfiguration configuration) {

        requireNonNull(configuration, new LostConfException("configuration为null"));
        requireNonNull(configuration.getExtConfig(), new LostConfException("ext配置为null"));
        requireNonNull(configuration.getSinkConfig(),new LostConfException("sink为null"));
        require(configuration.getSqlEntryList(),t->{
            return t.size()==0;
        },new LostConfException("没有query sql"));
        requireNonNull(configuration.getSourceConfig(),new LostConfException("source 为null"));
    }



    private void fillConfiguration(Statement statement, JstreamConfiguration jstreamConfiguration) throws Exception {
        requireNonNull(statement, "sql解析出现问题");
        if (statement instanceof CreateTable) {
            //定义souce sink
            CreateTable createTable = (CreateTable) statement;
            processCreateTable(createTable, jstreamConfiguration);
        } else if (statement instanceof SelectQuery) {
            //定义查询
            SelectQuery selectQuery = (SelectQuery) statement;
            processSelectQuery(selectQuery, jstreamConfiguration);
        } else if (statement instanceof InsertInto) {
            //定义插入
            InsertInto insertInto = (InsertInto) statement;
            processInsertInto(insertInto, jstreamConfiguration);

        } else if (statement instanceof Resources) {
            //定义一些资源 application name之类的
            Resources resources = (Resources) statement;
            processResource(resources, jstreamConfiguration);

        } else {
            throw new UnsupportSqlException("jstream暂不支持该类型:" + statement);
        }
    }

    private static final String APP_NAME = "APP_NAME";
    private static final String RESOURCE_MASTER = "RESOURCE_MASTER";
    private static final String RESOURCE_DRIVER_MEMORY = "RESOURCE_DRIVER_MEMORY";
    private static final String RESOURCE_EXECUTOR_MEMORY = "RESOURCE_EXECUTOR_MEMORY";
    private static final String RESOURCE_DRIVER_CORES = "RESOURCE_DRIVER_CORES";
    private static final String RESOURCE_NUM_EXECUTOR = "RESOURCE_NUM_EXECUTOR";

    private void processResource(Resources resources, JstreamConfiguration jstreamConfiguration) {
        List<Property> properties = resources.getProperties();

        ExtConfig extConfig = new ExtConfig();
        extConfig.setAppName(requireNotBlank(getValue(properties, APP_NAME),"app name为null"));

        jstreamConfiguration.setExtConfig(extConfig);

        ResouceConfig resouceConfig = new ResouceConfig();
        resouceConfig.setNumExecutors(getValueOrDefault(properties, RESOURCE_NUM_EXECUTOR, "2"));
        resouceConfig.setMaster(getValueOrDefault(properties, RESOURCE_MASTER, "yarn-cluster"));
        resouceConfig.setExecutorMemory(getValueOrDefault(properties, RESOURCE_EXECUTOR_MEMORY, "2G"));
        resouceConfig.setExecutorCores(getValueOrDefault(properties, RESOURCE_DRIVER_CORES, "2"));
        resouceConfig.setDriverMemory(getValueOrDefault(properties, RESOURCE_DRIVER_MEMORY, "1G"));
        jstreamConfiguration.setResouceConfig(resouceConfig);
    }


    private static final String OUT_MODE = "OUT_MODE";
    private static final String TRIGGER_PROCESSTIME = "TRIGGER_PROCESSTIME";
    private static final String TRIGGER_CONTINUOS = "TRIGGER_CONTINUOS";


    private void processInsertInto(InsertInto insertInto, JstreamConfiguration jstreamConfiguration) {
        String tableName = insertInto.getQualifiedName().toString();
        ColumnAlias columnAlias = insertInto.getColumnAlias();
        String query = insertInto.getQuery();

        //参数
        List<Property> properties = insertInto.getProperties();
        jstreamConfiguration.getSqlEntryList().add(new SqlEntry(query, "", null));

        //填充sink
        AbstractSinkConfig sinkConfig = jstreamConfiguration.getSinkConfig();
        OutPutModeConfig outPutModeConfig = new OutPutModeConfig(requireNonNull(getValue(properties, OUT_MODE), "with insert into 语句不能缺少OUT_MODE属性"));
        TriggerConfig triggerConfig = new TriggerConfig(getValue(properties, TRIGGER_PROCESSTIME) == null ? "" : getValue(properties, TRIGGER_PROCESSTIME)
                , getValue(properties, TRIGGER_CONTINUOS) == null ? "" : getValue(properties, TRIGGER_CONTINUOS));
        sinkConfig.setOutPutModeConfig(outPutModeConfig);
        sinkConfig.setTriggerConfig(triggerConfig);

    }


    private void processSelectQuery(SelectQuery selectQuery, JstreamConfiguration jstreamConfiguration) throws Exception {
        String alias = selectQuery.getAlias();
        String query = selectQuery.getQuery();
        requireNonNull(query);
        Optional<WaterMark> waterMark = selectQuery.getWaterMark();
        WaterMarkConfig waterMarkConfig = waterMark.map(water -> {
            return new WaterMarkConfig(water.getFiledName(), water.getExpression());
        }).orElseGet(() -> null);

        SqlEntry sqlEntry = new SqlEntry(query, alias == null ? "" : alias, waterMarkConfig);
        jstreamConfiguration.getSqlEntryList().add(sqlEntry);
    }

    /**
     * 填充jstream 中的source config
     */
    private void processCreateTable(CreateTable createTable, JstreamConfiguration jstreamConfiguration) throws Exception {
        if (createTable.getType() == CreateTable.Type.SOURCE) {
            processCreateTableSource(createTable, jstreamConfiguration);
        } else if (createTable.getType() == CreateTable.Type.SINK) {
            processCreateTableSink(createTable, jstreamConfiguration);
        }
    }

    private void processCreateTableSink(CreateTable createTable, JstreamConfiguration jstreamConfiguration) throws Exception {
        processCreateTableReal(createTable, jstreamConfiguration, "SINK");
    }

    private void processCreateTableSource(CreateTable createTable, JstreamConfiguration jstreamConfiguration) throws Exception {
        processCreateTableReal(createTable, jstreamConfiguration, "SOURCE");
    }

    private void processCreateTableReal
            (CreateTable createTable, JstreamConfiguration jstreamConfiguration, String extStr) throws Exception {
        String tableName = createTable.getName();
        List<Property> properties = createTable.getProperties();
        Class aClass = souceSinkMapConfig.get(getValue(properties, TYPE, extStr));
        requireNonNull(aClass);
        Config instance = (Config) aClass.newInstance();
        Set<Field> fieldSet = getAllFields(aClass, withAnnotation(Name.class));

        properties.stream().forEach(property -> {
            for (Field field : fieldSet) {
                Name name = field.getAnnotation(Name.class);
                if (StringUtils.equalsIgnoreCase(
                        property.getName().getValue().replaceAll("_", "."),
                        name.value())) {
                    try {
                        String value = property.getValue().unwarpExpressValue();
                        if (StringUtils.isNumeric(value)) {
                            BeanUtils.setProperty(instance, field.getName(), Integer.parseInt(value));
                        } else {
                            BeanUtils.setProperty(instance, field.getName(), value);
                        }

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        checkValue(aClass, instance);
        if (StringUtils.equalsIgnoreCase(extStr, "SOURCE")){
            KafkaSource.KafkaSouceConfig source = (KafkaSource.KafkaSouceConfig) instance;
            source.setAlias(tableName);
            jstreamConfiguration.setSourceConfig(source);
        }
        else
            jstreamConfiguration.setSinkConfig((AbstractSinkConfig) instance);
    }

    private void checkValue(Class cl, Config config) throws Exception {
        Set<Field> fieldSet = getAllFields(cl, withAnnotation(Name.class), withAnnotation(NotNull.class));
        for (Field field : fieldSet) {
            if (BeanUtils.getProperty(config, field.getName()) == null) {
                Name name = field.getAnnotation(Name.class);
                throw new BeanCheckException(name.value() + "配置项 在sql中 不能为null");
            }
        }
    }


    private String getValue(List<Property> properties, String key, String extStr) {
        for (Property property : properties) {
            if (StringUtils.equalsIgnoreCase(property.getName().toString(), key)) {

                String value = property.getValue().unwarpExpressValue();
                return StringUtils.upperCase(value + extStr);
            }
        }
        return null;

    }

    private String getValueOrDefault(List<Property> properties, String key, String defaultStr) {
        String value = getValue(properties, key);
        return value == null ? defaultStr : value;
    }

    private String getValue(List<Property> properties, String key) {
        for (Property property : properties) {
            if (StringUtils.equalsIgnoreCase(property.getName().toString(), key)) {
                String value = property.getValue().unwarpExpressValue();
                return value;
            }
        }
        return null;

    }
}
