package app.pooi.tenant.dynamicdatasource;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
class DynamicDataSourceMappingProvider implements InitializingBean {
    @Getter
    @Setter
    private static String MODULE = "";

    private final ConcurrentMap<String, String> mapping = new ConcurrentHashMap<>(8);

    public final DataSourceProperty property;

    public DynamicDataSourceMappingProvider(DataSourceProperty property) {
        this.property = property;
    }


    @Override
    public void afterPropertiesSet() {
        // 如果使用h2数据库此处数据库还未初始化完成，忽略报错
        reload(true);
    }

    public String getMappingValue(String key) {
        if (StringUtils.isBlank(key)) {
            log.warn("dbCode is blank, using default datasource");
            return null;
        }
        if (!mapping.containsKey(key)) {
            synchronized (this) {
                if (!mapping.containsKey(key)) {
                    reload(false);
                }
            }
        }
        return mapping.get(key);
    }

    private synchronized void reload(boolean init) {
        // 数据库加载数据源信息
        try (Connection conn = DriverManager.getConnection(property.getUrl(),
                property.getUsername(), property.getPassword());
             Statement stmt = conn.createStatement()
        ) {
            String querySql = "select * from t_tenant_db_mapping";
            var rs = stmt.executeQuery(querySql);

            while (rs.next()) {
                var applicationCode = rs.getString("tenant_code");
                var dbCode = rs.getString("db_code");
//                var module = rs.getString("module");
//                mapping.putIfAbsent(applicationCode, dbCode);
//                if (StringUtils.equalsIgnoreCase(module, MODULE)) {
                mapping.put(applicationCode, dbCode);
//                }
            }
        } catch (Exception ex) {
            if (!init) {
                log.error("加载动态数据源关系失败", ex);
            }
        }
    }
}
