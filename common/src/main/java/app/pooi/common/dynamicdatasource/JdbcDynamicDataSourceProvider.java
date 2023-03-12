package app.pooi.common.dynamicdatasource;

import app.pooi.common.prop.SpringShardingTableConfigurationProperties;
import com.baomidou.dynamic.datasource.ds.ItemDataSource;
import com.baomidou.dynamic.datasource.provider.AbstractJdbcDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.hikari.HikariCpConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.yaml.engine.YamlEngine;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
class JdbcDynamicDataSourceProvider extends AbstractJdbcDataSourceProvider {

    public final DataSourceProperty property;

    @Resource
    private SpringShardingTableConfigurationProperties shardingTableConfigurationProperties;

    public JdbcDynamicDataSourceProvider(DataSourceProperty property) {
        super(property.getDriverClassName(), property.getUrl(),
                property.getUsername(), property.getPassword());
        this.property = property;
    }

    @Override
    protected Map<String, DataSourceProperty> executeStmt(Statement statement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, DataSource> loadDataSources() {
        return this.loadDataSources(null);
    }

    Map<String, DataSource> loadDataSources(String datasourceKey) {
        // SPI加载
//        try {
//            Class.forName(property.getDriverClassName());
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
        var dataSourcePropertiesMap = new HashMap<String, DataSourceProperty>(8);
        // 数据库加载数据源信息
        try (Connection conn = DriverManager.getConnection(property.getUrl(),
                property.getUsername(), property.getPassword());
             Statement stmt = conn.createStatement()
        ) {
            String querySql = "select * from db_info";
            if (StringUtils.isNotBlank(datasourceKey)) {
                querySql += "where db_code = '" + datasourceKey + "'";
            }
            var rs = stmt.executeQuery(querySql);

            while (rs.next()) {
                var url = rs.getString("url");
                if (!StringUtils.startsWith(url, "jdbc:")) {
                    continue;
                }
                var dbCode = rs.getString("db_code");
                var username = rs.getString("username");
                var password = rs.getString("password");
                var driverClass = rs.getString("driver_class");
                dataSourcePropertiesMap.put(dbCode, buildProperty(url, username, password, driverClass));
            }
        } catch (Exception ex) {
            log.error("加载动态数据源信息失败", ex);
        }
        var dataSourceMap = super.createDataSourceMap(dataSourcePropertiesMap);
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


    private static DataSourceProperty buildProperty(String url, String username, String password, String driverClass) {
        var dataSourceProperty = new DataSourceProperty();
        dataSourceProperty.setUrl(url)
                .setUsername(username)
                .setPassword(password)
                .setDriverClassName(driverClass);

        var hikariCpConfig = new HikariCpConfig();
        hikariCpConfig.setMinIdle(0);
        hikariCpConfig.setMaxPoolSize(50);
        hikariCpConfig.setIdleTimeout(Duration.ofMinutes(5).toMillis());
        hikariCpConfig.setMaxLifetime(Duration.ofMinutes(10).toMillis());

        dataSourceProperty.setHikari(hikariCpConfig);
        return dataSourceProperty;
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
