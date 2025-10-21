package app.pooi.workflow.infrastructure.configuration.flowable.engine;

import app.pooi.tenant.multitenancy.ApplicationInfoHolder;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.persistence.deploy.DeploymentCache;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.Duration;
import java.util.Collection;
import java.util.List;

@Slf4j
public class ProcessDefinitionDeploymentCache<T> implements InitializingBean, DisposableBean, DeploymentCache<T> {

    @Resource
    @Setter
    private RedissonClient redissonClient;

    @Resource
    @Setter
    private ApplicationInfoHolder applicationInfoHolder;

    private Cache<String, T> cache;

    private RTopic topic;

    private Integer listenerId;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.cache = CacheBuilder.newBuilder().maximumSize(300).expireAfterWrite(Duration.ofMinutes(60)).build();

        if (this.redissonClient != null) {
            topic = this.redissonClient.getTopic("app:deploy_cache");
            listenerId = topic.addListener(DeploymentCacheMessage.class, (channel, msg) -> {
                log.info("topic receive message, channel: {} msg: {}", channel, msg);
                if (msg == null) {
                    return;
                }
                if ("CLEAR".equals(msg.getId())) {
                    List<String> invalidateKeys = this.cache.asMap().keySet().stream()
                            .filter(key -> key.startsWith(msg.applicationCode + ":"))
                            .toList();
                    this.cache.invalidateAll(invalidateKeys);
                } else {
                    this.cache.invalidate(getKey(msg.applicationCode, msg.id));
                }
            });
            log.info("addListener {}", listenerId);
        }
    }

    @Override
    public void destroy() throws Exception {
        if (topic != null && listenerId != null) {
            topic.removeListener(listenerId);
            log.info("removeListener {}", listenerId);
        }
    }

    private String getKey(String applicationCode, String id) {
        return applicationCode + ":" + id;
    }

    private String getKey(String id) {
        return getKey(applicationInfoHolder.getApplicationCode(), id);
    }

    @Override
    public T get(String id) {
        return cache.getIfPresent(getKey(id));
    }

    @Override
    public boolean contains(String id) {
        return cache.getIfPresent(getKey(id)) != null;
    }

    @Override
    public void add(String id, T object) {
        cache.put(getKey(id), object);
    }

    @Override
    public void remove(String id) {
        if (topic != null) {
            DeploymentCacheMessage message = new DeploymentCacheMessage()
                    .setApplicationCode(this.applicationInfoHolder.getApplicationCode())
                    .setId(id);
            topic.publish(message);
        } else {
            this.cache.invalidate(getKey(id));
        }
    }

    @Override
    public void clear() {
        if (topic != null) {
            DeploymentCacheMessage message = new DeploymentCacheMessage()
                    .setApplicationCode(this.applicationInfoHolder.getApplicationCode())
                    .setId("CLEAR");
            topic.publish(message);
        } else {
            this.cache.cleanUp();
        }
    }

    @Override
    public Collection<T> getAll() {
        return cache.asMap().values();
    }

    @Override
    public int size() {
        return (int) cache.size();
    }

    @Accessors(chain = true)
    @Data
    public static class DeploymentCacheMessage implements Serializable {

        private String applicationCode;

        private String id;

    }
}
