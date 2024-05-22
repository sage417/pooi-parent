package app.pooi.workflow.configuration.flowable.engine;

import app.pooi.common.multitenancy.ApplicationInfoHolder;
import app.pooi.common.util.SpringContextUtil;
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
import java.util.StringJoiner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Slf4j
class EventListenerTransactionSynchronization implements TransactionSynchronization {

    private final String procInstId;

    private final Executor executor;

    private final RedissonClient redissonClient;

    private final ApplicationInfoHolder applicationInfoHolder;

    private static final ThreadLocal<ListMultimap<String, Runnable>> TASKS = ThreadLocal.withInitial(ArrayListMultimap::create);

    public EventListenerTransactionSynchronization(String procInstId, Runnable runnableTask) {
        this.executor = DtpRegistry.getExecutor("event-push");
        this.procInstId = procInstId;
        this.redissonClient = SpringContextUtil.getBean(RedissonClient.class);
        this.applicationInfoHolder = SpringContextUtil.getBean(ApplicationInfoHolder.class);
        TASKS.get().put(procInstId, runnableTask);
    }

    @Override
    public void afterCommit() {
        List<Runnable> runnableList = TASKS.get().removeAll(this.procInstId);
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
            // ensure locked
            boolean acquireLock = ensureAsyncLock(fairLock.getName(), lockAsync, tId);
            orderlyExecute(runnableList, this.procInstId);
            unlock(acquireLock, fairLock, tId);
        });
    }

    private static void orderlyExecute(List<Runnable> runnableList, String procInstId) {
        for (Runnable runnable : runnableList) {
            try {
                runnable.run();
            } catch (Exception e) {
                log.error("procInst task error, proInstId:{}", procInstId, e);
            }
        }
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
        ListMultimap<String, Runnable> runnableListMultimap = TASKS.get();
        if (!runnableListMultimap.isEmpty()) {
            log.warn("TransactionSynchronization status: {}, clear procInst tasks: {}", status, runnableListMultimap.keySet());
            runnableListMultimap.clear();
        }
    }
}
