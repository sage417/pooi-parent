package app.pooi.workflow.application.service;

import app.pooi.workflow.TenantInfoHolderExtension;
import app.pooi.workflow.application.result.ProcessInstanceStartResult;
import app.pooi.workflow.domain.model.workflow.comment.Comment;
import app.pooi.workflow.domain.service.comment.CommentService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.spring.impl.test.FlowableSpringExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static app.pooi.workflow.TenantInfoHolderExtension.TENANT_APP_1;

@Slf4j
@ExtendWith(TenantInfoHolderExtension.class)
@ExtendWith(FlowableSpringExtension.class)
@SpringBootTest(classes = {})
class ProcessInstanceStartAppServiceTest {

    @Resource
    private ProcessInstanceStartAppService processInstanceStartAppService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private CommentService commentService;

    @Test
    @SneakyThrows
    @Deployment(resources = {"processes/article-workflow.bpmn20.xml"}, tenantId = TENANT_APP_1)
    void start() {

        Map<String, Object> variables = new HashMap<>();
        variables.put("author", "test@baeldung.com");
        variables.put("url", "http://baeldung.com/dummy");

        ProcessInstanceStartResult result = processInstanceStartAppService.start("articleReview", null, "", variables, "starter");

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(result.getProcessInstanceId()).singleResult();

        Assertions.assertThat(processInstance.getStartUserId()).isEqualTo("starter");

        List<Comment> comments = commentService.listByInstanceId(result.getProcessInstanceId());

        Assertions.assertThat(comments).hasSize(1);

    }
}