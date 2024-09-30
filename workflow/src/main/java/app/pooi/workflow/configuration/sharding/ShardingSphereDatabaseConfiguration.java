package app.pooi.workflow.configuration.sharding;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.ds.ItemDataSource;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.algorithm.core.config.AlgorithmConfiguration;
import org.apache.shardingsphere.infra.config.mode.ModeConfiguration;
import org.apache.shardingsphere.mode.repository.standalone.StandalonePersistRepositoryConfiguration;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.NoneShardingStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.single.api.config.SingleRuleConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class ShardingSphereDatabaseConfiguration {

    @Resource
    private DynamicRoutingDataSource dataSource;

    @Primary
    @Bean
    public DataSource shardingSphereDatasource() throws SQLException {
        Map<String, DataSource> dataSourceMap = dataSource.getDataSources().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> ((ItemDataSource) e.getValue()).getDataSource()));

        Properties persistRepositoryProps = new Properties();
        persistRepositoryProps.setProperty("provider", "H2");
//        persistRepositoryProps.setProperty("jdbc_url", "jdbc:h2:mem:config;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MYSQL");
//        persistRepositoryProps.setProperty("username", "SA");
        Properties shardingSphereJdbcProps = new Properties();
        shardingSphereJdbcProps.setProperty("sql-show", "true");
        shardingSphereJdbcProps.setProperty("check-table-metadata-enabled", "true");
        return ShardingSphereDataSourceFactory.createDataSource(
                new ModeConfiguration("Standalone", new StandalonePersistRepositoryConfiguration("JDBC", persistRepositoryProps)),
                dataSourceMap,
                Lists.newArrayList(createConfTableRuleConfiguration(), createFlowableTableRuleConfiguration(), createFlowableTableRuleConfiguration2()),
                shardingSphereJdbcProps);
    }

    @Bean
    public PlatformTransactionManager shardingSphereTransactionManager() throws SQLException {
        return new JdbcTransactionManager(shardingSphereDatasource());
    }

    private SingleRuleConfiguration createConfTableRuleConfiguration() {
        return new SingleRuleConfiguration(
                Lists.newArrayList("core.t_tenant_info", "core.t_tenant_db_info", "core.t_tenant_db_mapping"),
                null);
    }

    private ShardingRuleConfiguration createFlowableTableRuleConfiguration2() {
        ShardingRuleConfiguration result = new ShardingRuleConfiguration();
        result.getTables().add(new ShardingTableRuleConfiguration("ACT_GE_PROPERTY", "ds1.ACT_GE_PROPERTY"));
        result.getTables().add(new ShardingTableRuleConfiguration("ACT_GE_BYTEARRAY", "ds1.ACT_GE_BYTEARRAY"));

        result.setDefaultDatabaseShardingStrategy(new StandardShardingStrategyConfiguration("NAME_", "dynamicDbShardingAlg"));
        result.setDefaultTableShardingStrategy(new NoneShardingStrategyConfiguration());

        Properties props = new Properties();
        props.put("strategy", "STANDARD");
        props.put("algorithmClassName", DataBaseShardingAlgorithm.class.getName());
        result.getShardingAlgorithms().put("dynamicDbShardingAlg", new AlgorithmConfiguration("CLASS_BASED", props));
        return result;
    }

    private ShardingRuleConfiguration createFlowableTableRuleConfiguration() {
        ShardingRuleConfiguration result = new ShardingRuleConfiguration();
        result.getTables().add(new ShardingTableRuleConfiguration("ACT_RE_${['DEPLOYMENT', 'MODEL', 'PROCDEF']}", "ds1.ACT_RE_${['DEPLOYMENT', 'MODEL', 'PROCDEF']}"));
        result.getTables().add(new ShardingTableRuleConfiguration("ACT_RU_${['JOB', 'TIMER_JOB', 'SUSPENDED_JOB', 'EXTERNAL_JOB', 'HISTORY_JOB', 'DEADLETTER_JOB']}", "ds1.ACT_RU_${['JOB', 'TIMER_JOB', 'SUSPENDED_JOB', 'EXTERNAL_JOB', 'HISTORY_JOB', 'DEADLETTER_JOB']}"));
        result.getTables().add(new ShardingTableRuleConfiguration("ACT_RU_${['ENTITYLINK', 'EVENT_SUBSCR', 'EXECUTION', 'IDENTITYLINK', 'TASK', 'VARIABLE']}", "ds1.ACT_RU_${['ENTITYLINK', 'EVENT_SUBSCR', 'EXECUTION', 'IDENTITYLINK', 'TASK', 'VARIABLE']}"));
        result.getTables().add(new ShardingTableRuleConfiguration("ACT_HI_${['ACTINST', 'ATTACHMENT', 'COMMENT', 'ATTACHMENT', 'ATTACHMENT', 'ATTACHMENT', 'ATTACHMENT', 'ATTACHMENT', 'ATTACHMENT', 'ATTACHMENT', 'ATTACHMENT']}", "ds1.ACT_HI_${['ACTINST', 'ATTACHMENT', 'COMMENT', 'ATTACHMENT', 'ATTACHMENT', 'ATTACHMENT', 'ATTACHMENT', 'ATTACHMENT', 'ATTACHMENT', 'ATTACHMENT', 'ATTACHMENT']}"));

        result.setDefaultDatabaseShardingStrategy(new StandardShardingStrategyConfiguration("tenant_id", "dynamicDbShardingAlg"));
        result.setDefaultTableShardingStrategy(new NoneShardingStrategyConfiguration());

        Properties props = new Properties();
        props.put("strategy", "STANDARD");
        props.put("algorithmClassName", DataBaseShardingAlgorithm.class.getName());
        result.getShardingAlgorithms().put("dynamicDbShardingAlg", new AlgorithmConfiguration("CLASS_BASED", props));
        return result;
    }
}
