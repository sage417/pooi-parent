package app.pooi.workflow.configuration.flowable.modeler;

import org.flowable.ui.common.tenant.TenantProvider;

/**
 * @author sage417
 */
class MockTenantProvider implements TenantProvider {
    @Override
    public String getTenantId() {
        return "mock";
    }
}
