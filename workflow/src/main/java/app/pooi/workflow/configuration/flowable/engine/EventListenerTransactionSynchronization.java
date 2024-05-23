package app.pooi.workflow.configuration.flowable.engine;

import app.pooi.common.multitenancy.ApplicationInfoHolder;
import app.pooi.common.util.SpringContextUtil;
import app.pooi.workflow.repository.workflow.EventRecordDO;
import app.pooi.workflow.repository.workflow.EventRecordRepository;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.dromara.dynamictp.core.DtpRegistry;
import org.redisson.api.RFuture;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.transaction.support.TransactionSynchronization;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
class EventListenerTransactionSynchronization implements TransactionSynchronization {

    private final String procInstId;

    private final Executor executor;

    private final RedissonClient redissonClient;

    private final ApplicationInfoHolder applicationInfoHolder;

    private final EventRecordRepository eventRecordRepository;

    private static final ThreadLocal<ListMultimap<String, Supplier<EventRecordDO>>> TASKS = ThreadLocal.withInitial(ArrayListMultimap::create);

    public EventListenerTransactionSynchronization(String procInstId, Supplier<EventRecordDO> recordDOSupplier) {
        this.executor = DtpRegistry.getExecutor("event-push");
        this.procInstId = procInstId;
        this.redissonClient = SpringContextUtil.getBean(RedissonClient.class);
        this.applicationInfoHolder = SpringContextUtil.getBean(ApplicationInfoHolder.class);
        this.eventRecordRepository = SpringContextUtil.getBean(EventRecordRepository.class);
        TASKS.get().put(procInstId, recordDOSupplier);
    }

    @Override
    public void afterCommit() {
        List<Supplier<EventRecordDO>> runnableList = TASKS.get().removeAll(this.procInstId);
        if (runnableList.isEmpty()) {
            return;
        }
        String applicationCode = this.applicationInfoHolder.getApplicationCode();
        RLock fairLock = redissonClient.getFairLock(new StringJoiner(":").add("workflow").add("app").add(applicationCode)
                .add("event-push").add("procInst").add(this.procInstId).add("lock").toString());
        long tId = RandomUtils.nextLong();
        RFuture<Boolean> lockAsync = fairLock.tryLockAsync(240L, 180L, TimeUnit.SECONDS, tId);
        log.info("lockAsync lock: {}, tId: {}", fairLock.getName(), tId);
        executor.execute(() -> {
            List<EventRecordDO> eventRecordDOS = prepareEventDOs(runnableList, this.procInstId);
            // ensure locked
            boolean acquireLock = ensureAsyncLock(fairLock.getName(), lockAsync, tId);
            this.eventRecordRepository.saveBatch(eventRecordDOS, 50);
            unlock(acquireLock, fairLock, tId);
        });
    }

    private static List<EventRecordDO> prepareEventDOs(List<Supplier<EventRecordDO>> suppliers, String procInstId) {
        return suppliers.stream()
                .map(s -> {
                    try {
                        return s.get();
                    } catch (Exception e) {
                        log.error("procInst task error, proInstId:{}", procInstId, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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

    @Override
    public void afterCompletion(int status) {
        ListMultimap<String, ?> runnableListMultimap = TASKS.get();
        if (!runnableListMultimap.isEmpty()) {
            log.warn("TransactionSynchronization status: {}, clear procInst tasks: {}", status, runnableListMultimap.keySet());
            runnableListMultimap.clear();
        }
    }
}
