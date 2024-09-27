package app.pooi.workflow.configuration.flowable.module;

import org.flowable.ui.common.tenant.TenantProvider;

/**
 * @author yufeng.jin
 */
class MockTenantProvider implements TenantProvider {
    @Override
    public String getTenantId() {
        return "mock";
    }
}
