package app.pooi.common.dynamicdatasource;

import app.pooi.common.multitenancy.ApplicationInfoHolder;
import app.pooi.common.prop.SpringShardingTableConfigurationProperties;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.apache.shardingsphere.sharding.yaml.config.YamlShardingRuleConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotationUtils;

import javax.sql.DataSource;

@AutoConfigureBefore(value = com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration.class)
class DynamicDataSourceAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public ApplicationInfoHolder applicationInfoHolder() {
        return new ApplicationInfoHolder();
    }

    @ConditionalOnMissingBean
    @Bean
    public DynamicDataSourceProvider dynamicDataSourceProvider(DynamicDataSourceProperties properties) {
        final DataSourceProperty dataSourceProperty = properties.getDatasource().get(properties.getPrimary());
        return new JdbcDynamicDataSourceProvider(dataSourceProperty);
    }

    @ConditionalOnClass({YamlShardingSphereDataSourceFactory.class, YamlShardingRuleConfiguration.class})
    @EnableConfigurationProperties(value = SpringShardingTableConfigurationProperties.class)
    @Configuration
    static class ShardingJDBCDataSourceAutoConfiguration {

        @Bean
        public DynamicDataSourceProvider shardingDataSourceProvider(DynamicDataSourceProperties properties) {
            final DataSourceProperty dataSourceProperty = properties.getDatasource().get(properties.getPrimary());
            return new ShardingJdbcDynamicDataSourceProvider(dataSourceProperty);
        }
    }

    @Primary
    @Bean
    public DataSource dataSource(DynamicDataSourceProperties properties) {
        var routingDataSource = new TenantAwareDynamicRoutingDataSource();
        routingDataSource.setPrimary(properties.getPrimary());
        routingDataSource.setStrict(properties.getStrict());
        routingDataSource.setStrategy(properties.getStrategy());
        routingDataSource.setSeata(properties.getSeata());
        routingDataSource.setP6spy(properties.getP6spy());
        return routingDataSource;
    }

    @Bean
    public DynamicDataSourceMappingProvider dynamicDataSourceMappingProvider(DynamicDataSourceProperties properties) {
        final DataSourceProperty dataSourceProperty = properties.getDatasource().get(properties.getPrimary());
        return new DynamicDataSourceMappingProvider(dataSourceProperty);
    }

    @Bean
    public static InstantiationAwareBeanPostProcessor beanPostProcessor() {
        return new InstantiationAwareBeanPostProcessor() {
            @Override
            public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
                if (StringUtils.isNotBlank(DynamicDataSourceMappingProvider.getMODULE())) {
                    return null;
                }
                // 寻找注解
                var annotation = AnnotationUtils.findAnnotation(beanClass, EnableDynamicDataSource.class);
                // 第一次设置
                if (annotation != null && StringUtils.isNotBlank(annotation.module())
                        && StringUtils.isBlank(DynamicDataSourceMappingProvider.getMODULE())) {
                    DynamicDataSourceMappingProvider.setMODULE(annotation.module());
                }
                return null;
            }
        };
    }
}
