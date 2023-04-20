package app.pooi.common.dynamicdatasource;

import app.pooi.common.prop.SpringShardingTableConfigurationProperties;
import com.baomidou.dynamic.datasource.ds.ItemDataSource;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.yaml.engine.YamlEngine;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
class ShardingJdbcDynamicDataSourceProvider extends JdbcDynamicDataSourceProvider {

    @Resource
    private SpringShardingTableConfigurationProperties shardingTableConfigurationProperties;

    public ShardingJdbcDynamicDataSourceProvider(DataSourceProperty property) {
        super(property);
    }

    protected Map<String, DataSource> loadDataSources(String datasourceKey) {
        var dataSourceMap = super.loadDataSources(datasourceKey);
        // 分表配置
        var configurationPropertiesMap = MapUtils.emptyIfNull(shardingTableConfigurationProperties.getRootConfiguration());
        // 存在则构造数据源
        for (Map.Entry<String, DataSource> entry : dataSourceMap.entrySet()) {
            if (!configurationPropertiesMap.containsKey(entry.getKey())) {
                continue;
            }
            entry.setValue(buildShardingDataSource(configurationPropertiesMap.get(entry.getKey()), entry.getValue()));
        }
        return dataSourceMap;
    }




    @SneakyThrows
    private static DataSource buildShardingDataSource(SpringShardingTableConfigurationProperties.SpringYamlRootConfigurationProperties rootConfigurationProperties,
                                                      DataSource dataSource) {
        if (dataSource instanceof ItemDataSource) {
            dataSource = ((ItemDataSource) dataSource).getDataSource();
        }
        var yamlRootConfiguration = SpringShardingTableConfigurationProperties.convertToYamlRootConfiguration(rootConfigurationProperties);
        var bytes = YamlEngine.marshal(yamlRootConfiguration).getBytes(StandardCharsets.UTF_8);
        return YamlShardingSphereDataSourceFactory.createDataSource(dataSource, bytes);
    }
}
