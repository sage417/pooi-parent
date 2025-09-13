package app.pooi.workflow.application.eventpush;

import app.pooi.rpc.workflow.stubs.HelloWorldRequest;
import app.pooi.rpc.workflow.stubs.HelloWorldResponse;
import app.pooi.workflow.domain.repository.EventPushProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.MethodDescriptor;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.ClientCalls;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GenericGrpcInvoker {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final EventPushProfileRepository configRepository;
    private final GrpcChannelManager channelManager;

    /**
     * 根据配置键动态调用gRPC服务
     */
    public <T> T invokeByConfigKey(String tenantId) {
//        EventPushProfile profile = configRepository.findByTenantId(tenantId);
//        if (profile == null) {
//            return null;
//        }

        try {
            // TODO parse target from profile
            Channel channel = channelManager.getChannel("discovery:///pooi-workflow-core");

            // 动态调用
            MethodDescriptor<HelloWorldRequest, HelloWorldResponse> methodDescriptor =
                    buildMethodDescriptor("app.pooi.rpc.workflow.HelloWorldService", "SayHello");

            HelloWorldRequest request = HelloWorldRequest.newBuilder()
                    .setName("your name")
                    .build();

            HelloWorldResponse response = ClientCalls.blockingUnaryCall(
                    channel,
                    methodDescriptor,
                    CallOptions.DEFAULT.withDeadlineAfter(5000, TimeUnit.MILLISECONDS),
                    request
            );

            return (T) response;
        } catch (Exception e) {
            throw new RuntimeException("gRPC调用失败", e);
        }
    }

    private MethodDescriptor<HelloWorldRequest, HelloWorldResponse> buildMethodDescriptor(String fullServiceName, String methodName) {

        return MethodDescriptor.<HelloWorldRequest, HelloWorldResponse>newBuilder()
                .setType(MethodDescriptor.MethodType.UNARY)
                .setFullMethodName(MethodDescriptor.generateFullMethodName(fullServiceName, methodName))
                .setRequestMarshaller(ProtoUtils.marshaller(HelloWorldRequest.getDefaultInstance()))
                .setResponseMarshaller(ProtoUtils.marshaller(HelloWorldResponse.getDefaultInstance()))
                .build();
    }

}