package app.pooi.workflow;

import lombok.NoArgsConstructor;
import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author yufeng.jin
 */
@NoArgsConstructor
public class TenantInfoHolderExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        TenantInfoHolder tenantInfoHolder = createTenantInfoHolder(extensionContext);
        tenantInfoHolder.clearCurrentTenantId();
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        TenantInfoHolder tenantInfoHolder = createTenantInfoHolder(extensionContext);
        tenantInfoHolder.setCurrentTenantId("app1");
    }

    protected TenantInfoHolder createTenantInfoHolder(ExtensionContext context) {
        return SpringExtension.getApplicationContext(context).getBean(TenantInfoHolder.class);
    }

}
