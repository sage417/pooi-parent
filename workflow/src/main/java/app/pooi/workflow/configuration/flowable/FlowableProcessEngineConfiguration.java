package app.pooi.workflow.configuration.flowable;

import app.pooi.workflow.configuration.flowable.behavior.CustomActivityBehaviorFactory;
import app.pooi.workflow.configuration.flowable.engine.WorkflowFlowableEngineEventListener;
import com.google.common.collect.Lists;
import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
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
    public AbstractFlowableEngineEventListener engineEventListener() {
        return new WorkflowFlowableEngineEventListener();
    }

    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> configurationConfigurer(DataSource dataSource) {
        return conf -> {
            // 注意某些属性不生效 如disable idm
            conf.setDataSource(dataSource);
            conf.setDatabaseSchemaUpdate("true");
            conf.setEnableHistoricTaskLogging(true);

            conf.setActivityBehaviorFactory(new CustomActivityBehaviorFactory());
            //
            conf.setEventListeners(Lists.newArrayList(engineEventListener()));
            //
//            conf.setCustomMybatisXMLMappers(Sets.newHashSet("custom-mappers/AttachmentMapper.xml"));
        };
    }

}
