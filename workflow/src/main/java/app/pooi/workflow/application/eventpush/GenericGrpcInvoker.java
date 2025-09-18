package app.pooi.workflow.application.eventpush;

import app.pooi.rpc.workflow.stubs.HelloWorldRequest;
import app.pooi.rpc.workflow.stubs.HelloWorldResponse;
import io.grpc.*;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.ClientCalls;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GenericGrpcInvoker {

    private final GrpcChannelManager channelManager;

    /**
     * unary call with fixed request and response obj
     *
     * @param target channel target
     * @param fullServiceName service name
     * @param methodName method name
     * @param request request body
     */
    public <T> T unaryCall(String target, String fullServiceName, String methodName, HelloWorldRequest request) {

        try {
            Channel channel = channelManager.getChannel(target);

            MethodDescriptor<HelloWorldRequest, HelloWorldResponse> methodDescriptor =
                    buildMethodDescriptor(fullServiceName, methodName);

            HelloWorldResponse response = ClientCalls.blockingUnaryCall(
                    channel,
                    methodDescriptor,
                    CallOptions.DEFAULT.withDeadlineAfter(5000, TimeUnit.MILLISECONDS),
                    request
            );

            return (T) response;
        } catch (Exception e) {
            throw new RuntimeException("rpc call failed", e);
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

    @Component
    static
    class GrpcChannelManager {

        private final Map<String, ManagedChannel> channelCache = new ConcurrentHashMap<>();

        public ManagedChannel getChannel(String target) {

            ManagedChannel channel = channelCache.get(target);
            if (channel != null) {
                return channel;
            }

            channel = createChannel(target);
            channelCache.put(target, channel);
            return channel;
        }

        private ManagedChannel createChannel(String target) {

            ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forTarget(target);

            if (StringUtils.startsWithIgnoreCase(target, "static")) {
                builder.usePlaintext();
            }
            builder.usePlaintext();

            return builder.build();
        }

        public void refreshChannel(String configKey) {
            ManagedChannel channel = channelCache.remove(configKey);
            if (channel != null) {
                channel.shutdown();
            }
        }

        @PreDestroy
        public void shutdown() {
            channelCache.values().forEach(ManagedChannel::shutdown);
            channelCache.clear();
        }
    }
}