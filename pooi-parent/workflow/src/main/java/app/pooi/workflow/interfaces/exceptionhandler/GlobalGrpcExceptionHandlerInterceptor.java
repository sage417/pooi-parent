package app.pooi.workflow.interfaces.exceptionhandler;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@GrpcGlobalServerInterceptor
class GlobalGrpcExceptionHandlerInterceptor implements ServerInterceptor {


    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        ServerCall<ReqT, RespT> wrappedCall = new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void close(Status status, Metadata trailers) {
                if (!status.isOk()) {
                    log.error("gRPC call error: code={}, description={}", status.getCode(), status.getDescription());
                }
                super.close(status, trailers);
            }
        };


        ServerCall.Listener<ReqT> listener = next.startCall(wrappedCall, headers);

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(listener) {
            @Override
            public void onHalfClose() {
                try {
                    super.onHalfClose();
                } catch (Throwable ex) {
                    handleException(call, ex);
                }
            }

            @Override
            public void onReady() {
                try {
                    super.onReady();
                } catch (Throwable ex) {
                    handleException(call, ex);
                }
            }
        };
    }

    private <ReqT, RespT> void handleException(ServerCall<ReqT, RespT> call, Throwable ex) {
        Status status;

        if (ex instanceof IllegalArgumentException) {
            status = Status.INVALID_ARGUMENT.withDescription(ex.getMessage());
        } else if (ex instanceof IllegalStateException) {
            status = Status.FAILED_PRECONDITION.withDescription(ex.getMessage());
        } else if (ex instanceof NullPointerException) {
            status = Status.INVALID_ARGUMENT.withDescription("npe");
        } else {
            status = Status.INTERNAL.withDescription("server internal error: " + ex.getMessage());
            log.error("server internal error", ex);
        }

        call.close(status, new Metadata());
    }
}
