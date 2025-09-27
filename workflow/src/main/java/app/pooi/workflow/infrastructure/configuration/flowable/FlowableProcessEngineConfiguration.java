/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package app.pooi.workflow.infrastructure.configuration.flowable;

import app.pooi.workflow.infrastructure.configuration.flowable.behavior.CustomActivityBehaviorFactory;
import app.pooi.workflow.infrastructure.configuration.flowable.engine.ProcessDefinitionDeploymentCache;
import app.pooi.workflow.infrastructure.configuration.flowable.engine.WorkflowFlowableEngineEventListener;
import app.pooi.workflow.infrastructure.configuration.flowable.props.FlowableCustomProperties;
import com.google.common.collect.Lists;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.flowable.common.engine.impl.persistence.deploy.DeploymentCache;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.flowable.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@EnableConfigurationProperties({FlowableCustomProperties.class})
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
    public DeploymentCache<ProcessDefinitionCacheEntry> deploymentCache() {
        return new ProcessDefinitionDeploymentCache<>();
    }

    @Bean
    public ActivityBehaviorFactory customActivityBehaviorFactory() {
        return new CustomActivityBehaviorFactory();
    }

    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> configurationConfigurer(DataSource dataSource) {
        return conf -> {
            // 注意某些属性不生效 如disable idm
            // force database type = mysql
            conf.setDatabaseType(AbstractEngineConfiguration.DATABASE_TYPE_MYSQL);
            conf.setDataSource(dataSource);
            conf.setDatabaseSchemaUpdate("true");
            conf.setEnableHistoricTaskLogging(true);

            conf.setActivityBehaviorFactory(customActivityBehaviorFactory());
            // 事件监听
            conf.setEventListeners(Lists.newArrayList(engineEventListener()));
            // 缓存
//            conf.setProcessDefinitionCache(deploymentCache());
            conf.setCreateUserTaskInterceptor(new FlowableCreateUserTaskInterceptor());
            //
//            conf.setCustomMybatisXMLMappers(Sets.newHashSet("custom-mappers/AttachmentMapper.xml"));
        };
    }

}
