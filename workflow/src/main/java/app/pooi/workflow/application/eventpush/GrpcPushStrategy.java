package app.pooi.workflow.application.eventpush;

import app.pooi.rpc.workflow.stubs.HelloWorldRequest;
import app.pooi.workflow.domain.model.workflow.eventpush.EventPushProfile;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.eventpush.EventRecordEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
class GrpcPushStrategy implements PushStrategy {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private GenericGrpcInvoker genericGrpcInvoker;

    @SneakyThrows
    @Override
    public void push(@NonNull EventPushProfile eventPushProfile, @NonNull EventRecordEntity eventRecordDO) {
        String grpcProfile = eventPushProfile.getProfile();
        JsonNode profileNode = objectMapper.readTree(StringEscapeUtils.unescapeJson(StringUtils.strip(grpcProfile, "\"")));

        String target = profileNode.get("target").asText();
        String fullServiceName = profileNode.get("fullServiceName").asText();
        String methodName = profileNode.get("methodName").asText();

        HelloWorldRequest request = HelloWorldRequest.newBuilder().setName(objectMapper.writeValueAsString(eventRecordDO)).build();

        genericGrpcInvoker.unaryCall(target, fullServiceName, methodName, request);
    }
}
