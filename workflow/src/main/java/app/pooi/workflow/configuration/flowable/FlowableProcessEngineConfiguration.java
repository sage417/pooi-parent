package app.pooi.workflow.configuration.flowable;

import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
class FlowableProcessEngineConfiguration {

    @Bean
    public TenantInfoHolder tenantInfoHolder() {
        return new MultiTenantInfoHolder();
    }

    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> configurationConfigurer(DataSource dataSource) {
        return processEngineConfiguration -> {
            // 注意某些属性不生效 如disable idm
            processEngineConfiguration.setDataSource(dataSource);
            processEngineConfiguration.setDatabaseSchemaUpdate("true");
        };
    }

}
