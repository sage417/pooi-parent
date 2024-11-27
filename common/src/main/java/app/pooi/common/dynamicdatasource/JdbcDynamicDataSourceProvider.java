package app.pooi.common.dynamicdatasource;

import com.baomidou.dynamic.datasource.provider.AbstractJdbcDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.hikari.HikariCpConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
class JdbcDynamicDataSourceProvider extends AbstractJdbcDataSourceProvider {

    protected final DataSourceProperty property;

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

    protected Map<String, DataSource> loadDataSources(String datasourceKey) {
        var dataSourcePropertiesMap = new HashMap<String, DataSourceProperty>(8);
        // 数据库加载数据源信息
        try (Connection conn = DriverManager.getConnection(property.getUrl(),
                property.getUsername(), property.getPassword());
             Statement stmt = conn.createStatement()
        ) {
            String querySql = "select * from t_tenant_db_info";
            if (StringUtils.isNotBlank(datasourceKey)) {
                querySql += " where db_code = '" + datasourceKey + "'";
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
                var schemaSql = rs.getString("schema_sql");
                dataSourcePropertiesMap.put(dbCode, buildProperty(url, username, password, driverClass, schemaSql));
            }
        } catch (Exception ex) {
            log.error("加载动态数据源信息失败", ex);
        }
        Map<String, DataSource> dataSourceMap = Map.of();
        try {
            dataSourceMap = super.createDataSourceMap(dataSourcePropertiesMap);
        } catch (Exception ex) {
            log.warn("创建数据源失败", ex);
        }
        return dataSourceMap;
    }


    private static DataSourceProperty buildProperty(String url, String username, String password, String driverClass, String schemaSql) {
        var dataSourceProperty = new DataSourceProperty();
        dataSourceProperty.setUrl(url)
                .setUsername(username)
                .setPassword(password)
                .setDriverClassName(driverClass)
                .setLazy(Boolean.FALSE);
        
        if (StringUtils.isNotBlank(schemaSql)) {
            dataSourceProperty.getInit().setSchema(schemaSql);
        }
        
        var hikariCpConfig = new HikariCpConfig();
        hikariCpConfig.setMinIdle(0);
        hikariCpConfig.setMaxPoolSize(50);
        hikariCpConfig.setConnectionTimeout(Duration.ofMillis(600).toMillis());
        hikariCpConfig.setIdleTimeout(Duration.ofMinutes(5).toMillis());
        hikariCpConfig.setMaxLifetime(Duration.ofMinutes(10).toMillis());

        dataSourceProperty.setHikari(hikariCpConfig);
        return dataSourceProperty;
    }

}
