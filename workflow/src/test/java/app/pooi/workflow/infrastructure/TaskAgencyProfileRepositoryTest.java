package app.pooi.workflow.infrastructure;


import app.pooi.workflow.TenantInfoHolderExtension;
import app.pooi.workflow.domain.model.workflow.agency.TaskAgencyProfile;
import app.pooi.workflow.domain.repository.TaskAgencyProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@ExtendWith(TenantInfoHolderExtension.class)
@SpringBootTest
public class TaskAgencyProfileRepositoryTest {

    @Resource
    private TaskAgencyProfileRepository taskAgencyProfileRepository;

    @Resource
    private TenantInfoHolder tenantInfoHolder;

    @Test
    public void testSelectValidByProcessDefinitionKeyAndTenantId() {
        List<TaskAgencyProfile> taskAgencyProfiles = taskAgencyProfileRepository.selectValidByProcessDefinitionKeyAndTenantId("", tenantInfoHolder.getCurrentTenantId());
        Assertions.assertThat(taskAgencyProfiles).hasSize(1);
    }
}
