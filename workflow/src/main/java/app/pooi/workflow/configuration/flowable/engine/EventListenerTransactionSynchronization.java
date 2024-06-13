package app.pooi.workflow.configuration.flowable.engine;

import app.pooi.common.multitenancy.ApplicationInfoHolder;
import app.pooi.common.util.SpringContextUtil;
import app.pooi.model.workflow.event.EventPayload;
import app.pooi.model.workflow.event.Header;
import app.pooi.model.workflow.event.WorkFlowEvent;
import app.pooi.workflow.repository.workflow.EventRecordDO;
import app.pooi.workflow.repository.workflow.EventRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import lombok.SneakyThrows;
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
public class EventListenerTransactionSynchronization implements TransactionSynchronization {
    private static final ThreadLocal<ListMultimap<String, Supplier<EventRecordDO>>> TL_EVENT_RECORD_SUPPLIER =
            ThreadLocal.withInitial(ArrayListMultimap::create);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String procInstId;

    private final Executor executor;

    private final RedissonClient redissonClient;

    private final ApplicationInfoHolder applicationInfoHolder;

    private final EventRecordRepository eventRecordRepository;

    private final WorkFlowEventFactory workFlowEventFactory;


    public EventListenerTransactionSynchronization(String procInstId, Supplier<EventRecordDO> recordDOSupplier) {
        this.executor = DtpRegistry.getExecutor("event-push");
        this.procInstId = procInstId;
        this.redissonClient = SpringContextUtil.getBean(RedissonClient.class);
        this.applicationInfoHolder = SpringContextUtil.getBean(ApplicationInfoHolder.class);
        this.eventRecordRepository = SpringContextUtil.getBean(EventRecordRepository.class);
        this.workFlowEventFactory = SpringContextUtil.getBean(WorkFlowEventFactory.class);
        TL_EVENT_RECORD_SUPPLIER.get().put(procInstId, recordDOSupplier);
    }

    @Override
    public void afterCommit() {
        List<Supplier<EventRecordDO>> runnableList = TL_EVENT_RECORD_SUPPLIER.get().removeAll(this.procInstId);
        if (runnableList.isEmpty()) {
            return;
        }
        // 记录event record
        List<EventRecordDO> eventRecordDOS = prepareEventDOs(runnableList, this.procInstId);
        this.eventRecordRepository.saveBatch(eventRecordDOS, 50);

        String applicationCode = this.applicationInfoHolder.getApplicationCode();
        RLock fairLock = redissonClient.getFairLock(new StringJoiner(":").add("workflow").add("app").add(applicationCode)
                .add("event-push").add("procInst").add(this.procInstId).add("lock").toString());
        long tId = RandomUtils.nextLong();
        RFuture<Boolean> lockAsync = fairLock.tryLockAsync(240L, 180L, TimeUnit.SECONDS, tId);
        log.info("lockAsync lock: {}, tId: {}", fairLock.getName(), tId);
        executor.execute(() -> asyncExecute(fairLock, lockAsync, tId, eventRecordDOS));
    }

    @SneakyThrows
    private void asyncExecute(RLock fairLock, RFuture<Boolean> lockAsync, long tId, List<EventRecordDO> eventRecordDOS) {
        // ensure locked
        boolean acquireLock = ensureAsyncLock(fairLock.getName(), lockAsync, tId);
        for (EventRecordDO eventRecordDO : eventRecordDOS) {
            WorkFlowEvent workFlowEvent = this.workFlowEventFactory.buildEvent(eventRecordDO);
            EventPayload eventPayload = new EventPayload().setHeader(new Header(eventRecordDO.getEventId(), eventRecordDO.getEventType()))
                    .setEvent(workFlowEvent);
            String bodyString = objectMapper.writeValueAsString(eventPayload);
            log.info("event payload: {}", bodyString);
        }
        unlock(acquireLock, fairLock, tId);
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
        ListMultimap<String, ?> runnableListMultimap = TL_EVENT_RECORD_SUPPLIER.get();
        if (!runnableListMultimap.isEmpty()) {
            log.warn("TransactionSynchronization status: {}, clear procInst tasks: {}", status, runnableListMultimap.keySet());
            runnableListMultimap.clear();
        }
    }
}
