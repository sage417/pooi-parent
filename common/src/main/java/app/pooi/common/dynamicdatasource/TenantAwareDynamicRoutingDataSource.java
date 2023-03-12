package app.pooi.common.dynamicdatasource;

import app.pooi.common.multitenancy.ApplicationInfoHolder;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

class TenantAwareDynamicRoutingDataSource extends DynamicRoutingDataSource {

    @Resource
    private ApplicationInfoHolder applicationInfoHolder;

    @Resource
    private DynamicDataSourceMappingProvider mappingProvider;

    @Resource
    private List<DynamicDataSourceProvider> providers;


    @Override
    public DataSource determineDataSource() {
        // 优先从注解中获取
        var dataSourceKey = StringUtils.defaultIfBlank(DynamicDataSourceContextHolder.peek(),
                mappingProvider.getMappingValue(applicationInfoHolder.getApplicationCode()));

        if (StringUtils.isNotBlank(dataSourceKey) && !getDataSources().containsKey(dataSourceKey)) {
            loadDataSource(dataSourceKey);
        }
        return getDataSource(dataSourceKey);
    }

    private synchronized void loadDataSource(String key) {
        if (StringUtils.isBlank(key) || getDataSources().containsKey(key)) {
            return;
        }
        var dataSourceProvider = providers.stream().filter(JdbcDynamicDataSourceProvider.class::isInstance)
                .findFirst();
        if (dataSourceProvider.isEmpty()) {
            return;
        }
        var jdbcDynamicDataSourceProvider = (JdbcDynamicDataSourceProvider) dataSourceProvider.get();
        var dataSourceMap = jdbcDynamicDataSourceProvider.loadDataSources(key);
        var dataSource = dataSourceMap.get(key);
        // 加载成功
        if (dataSource != null) {
            this.addDataSource(key, dataSource);
        }
    }
}
