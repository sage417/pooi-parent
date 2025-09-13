package app.pooi.workflow.application.eventpush;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
class GrpcChannelManager {

    private final Map<String, ManagedChannel> channelCache = new ConcurrentHashMap<>();

    public ManagedChannel getChannel(String target) {

        // 先从缓存获取
        ManagedChannel channel = channelCache.get(target);
        if (channel != null) {
            return channel;
        }

        // 创建新通道
        channel = createChannel(target);
        channelCache.put(target, channel);
        return channel;
    }

    private ManagedChannel createChannel(String target) {

        ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forTarget(target);

        if (StringUtils.startsWithIgnoreCase(target, "static")) {
            builder.usePlaintext(); // 静态配置通常用于开发环境
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
