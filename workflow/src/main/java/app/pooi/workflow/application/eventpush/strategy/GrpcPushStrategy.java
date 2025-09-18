package app.pooi.workflow.application.eventpush.strategy;

import app.pooi.rpc.workflow.stubs.HelloWorldRequest;
import app.pooi.workflow.application.eventpush.GenericGrpcInvoker;
import app.pooi.workflow.application.eventpush.PushStrategy;
import app.pooi.workflow.domain.model.workflow.eventpush.EventPushProfile;
import app.pooi.workflow.domain.model.workflow.eventpush.EventRecord;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
class GrpcPushStrategy extends AbstractReadProfileStrategy implements PushStrategy {

    @Resource
    private GenericGrpcInvoker genericGrpcInvoker;

    @SneakyThrows
    @Override
    public void push(@NonNull EventPushProfile eventPushProfile, @NonNull EventRecord eventRecordDO) {

        // read push profile
        String grpcProfileContent = eventPushProfile.getProfile();
        String unescapeJson = StringEscapeUtils.unescapeJson(StringUtils.strip(grpcProfileContent, "\""));
        GrpcProfile grpcProfile = objectMapper.readValue(unescapeJson, GrpcProfile.class);

        // build request
        HelloWorldRequest request = HelloWorldRequest.newBuilder()
                .setName(objectMapper.writeValueAsString(eventRecordDO))
                .build();

        // unary call
        genericGrpcInvoker.unaryCall(grpcProfile.target(), grpcProfile.fullServiceName(), grpcProfile.methodName(), request);
    }

    record GrpcProfile(String target, String fullServiceName, String methodName) {
    }
}
