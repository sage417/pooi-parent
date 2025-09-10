package app.pooi.workflow.application.eventpush;

import app.pooi.basic.workflow.event.EventPayload;
import app.pooi.tenant.multitenancy.ApplicationInfoHolder;
import app.pooi.workflow.repository.workflow.EventPushProfileDO;
import app.pooi.workflow.repository.workflow.EventPushProfileRepository;
import app.pooi.workflow.repository.workflow.EventRecordDO;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RFuture;
import org.redisson.api.RLock;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

@Slf4j
@Component
public class EventPushApplication {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Resource(name = "event-push")
    private Executor executor;

    @Resource
    private EventPushProfileRepository eventPushProfileRepository;

    @Resource
    private ApplicationInfoHolder applicationInfoHolder;

    @Resource
    private Map<String, PushStrategy> pushStrategies;

    public void asyncExecute(RLock fairLock, RFuture<Boolean> lockAsync, long tId, List<EventRecordDO> eventRecordDOS) {
        executor.execute(() -> {
            // ensure locked
            boolean acquireLock = ensureAsyncLock(fairLock.getName(), lockAsync, tId);
            try {
                pushOrderly(eventRecordDOS);
            } finally {
                unlock(acquireLock, fairLock, tId);
            }
        });
    }

    @SneakyThrows
    private void pushOrderly(List<EventRecordDO> eventRecordDOS) {
        EventPushProfileDO profileDO = eventPushProfileRepository.getOne(
                Wrappers.lambdaQuery(EventPushProfileDO.class)
                        .eq(EventPushProfileDO::getTenantId, applicationInfoHolder.getApplicationCode())
        );

        if (profileDO == null) {
            return;
        }

        PushStrategy pushStrategy = getPushStrategy(profileDO.getType());

        for (EventRecordDO eventRecordDO : eventRecordDOS) {
            EventPayload eventPayload = objectMapper.readValue(eventRecordDO.getEvent(), EventPayload.class);
            String bodyString = objectMapper.writeValueAsString(eventPayload);
            log.info("event payload: {}", bodyString);

            pushStrategy.push(eventRecordDO);
        }
    }

    private PushStrategy getPushStrategy(String strategyName) {
        PushStrategy pushStrategy = pushStrategies.get(strategyName + "PushStrategy");
        if (pushStrategy == null) {
            throw new IllegalArgumentException("No push strategy registered for " + strategyName);
        }
        return pushStrategy;
    }

    private static void unlock(boolean acquireLock, RLock fairLock, long tId) {
        if (fairLock == null || !acquireLock) {
            return;
        }
        if (fairLock.isHeldByThread(tId)) {
            fairLock.unlockAsync(tId)
                    .thenAccept(v -> log.info("lockAsync unlock lock: {}, tId: {}", fairLock.getName(), tId))
                    .exceptionally(e -> {
                        log.error("unlock failed lock: {}", fairLock.getName(), e);
                        fairLock.forceUnlockAsync();
                        return null;
                    });
        } else {
            log.error("validation failed skip unlock, lock: {}, tId: {}", fairLock.getName(), tId);
        }
    }

    private static boolean ensureAsyncLock(String name, RFuture<Boolean> lockAsync, long tId) {
        if (lockAsync == null) {
            log.warn("lockAsync is null, lock: {} tId: {}", name, tId);
            return false;
        }
        try {
            Boolean acquired = lockAsync.get();
            if (!acquired) {
                log.warn("lockAsync timeout lock: {} tId: {}", name, tId);
            } else {
                log.info("lockAsync acquired lock: {}, tId: {}", name, tId);
            }
            return acquired;
        } catch (InterruptedException e) {
            log.warn("lockAsync interrupted lock: {} tId: {}", name, tId);
            Thread.currentThread().interrupt();

        } catch (ExecutionException e) {
            log.error("lockAsync failed lock: {} tId: {}", name, tId, e);
        }
        return false;
    }
}
