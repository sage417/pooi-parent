package app.pooi.workflow.configuration.flowable;

import app.pooi.common.multitenancy.ApplicationInfo;
import app.pooi.common.multitenancy.ApplicationInfoHolder;
import app.pooi.workflow.repository.tenant.TenantInfoDO;
import app.pooi.workflow.repository.tenant.TenantInfoMapper;
import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class MultiTenantInfoHolder implements TenantInfoHolder, ApplicationContextAware {

    @Resource
    private ApplicationInfoHolder applicationInfoHolder;

    @Resource
    private TenantInfoMapper tenantInfoMapper;

    private ApplicationContext applicationContext;

    private final ConcurrentMap<String, Boolean> knownTenants = new ConcurrentHashMap<>();

    @Override
    public Collection<String> getAllTenants() {
        var tenantInfoDOS = this.tenantInfoMapper.selectList(null);
        return tenantInfoDOS.stream().map(TenantInfoDO::getTenantCode).toList();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setCurrentTenantId(String currentTenantId) {
        knownTenants.put(currentTenantId, Boolean.TRUE);
        applicationInfoHolder.setApplicationInfo(
                new ApplicationInfo().setApplicationCode(currentTenantId));
    }

    @Override
    public String getCurrentTenantId() {
        return applicationInfoHolder.getApplicationCode();
    }

    @Override
    public void clearCurrentTenantId() {
        applicationInfoHolder.clearApplicationInfo();
    }

    /**
     * 外部设置tenantId
     * 若没有注册过该租户，尝试注册
     *
     * @param tenantId 租户id
     */
    public void setAndRegisterCurrentTenantId(String tenantId) {
        this.setCurrentTenantId(tenantId);
        if (!knownTenants.containsKey(tenantId)) {
            var objectProvider = applicationContext.getBeanProvider(MultiSchemaMultiTenantProcessEngineConfiguration.class);
            objectProvider.ifAvailable(conf-> conf.registerTenant(tenantId, null));
            knownTenants.put(tenantId, Boolean.TRUE);
        }
    }
}
