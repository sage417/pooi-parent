package app.pooi.workflow.util;

import app.pooi.common.util.CollectorsUtil;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.common.engine.impl.persistence.cache.EntityCache;
import org.flowable.engine.impl.persistence.entity.ActivityInstanceEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.runtime.ActivityInstance;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BpmnModelUtil {

    public static void bfs(@NonNull BpmnModel bpmnModel,
                           @NonNull String startNodeId,
                           @NonNull Consumer<FlowElement> visitor) {
        var startNode = ((FlowNode) bpmnModel.getFlowElement(startNodeId));
        if (startNode == null) {
            return;
        }
        ArrayDeque<FlowElement> deque = new ArrayDeque<>();
        deque.push(startNode);
        Set<FlowElement> visitedFlowElements = new HashSet<>();

        while (!deque.isEmpty()) {
            List<FlowNode> flowNodes = new ArrayList<>();
            for (FlowElement flowElement : deque) {
                if (!visitedFlowElements.add(flowElement)) {
                    continue;
                }
                if (flowElement instanceof FlowNode) {
                    flowNodes.add(((FlowNode) flowElement));
                }
            }
            deque.clear();

            for (FlowNode flowNode : flowNodes) {
                visitor.accept(flowNode);

                flowNode.getOutgoingFlows().stream()
                        .map(SequenceFlow::getTargetFlowElement)
                        .filter(Objects::nonNull)
                        .distinct()
                        .forEach(deque::addLast);

                flowNode.getOutgoingFlows().forEach(visitor);
            }
        }
    }

    public static boolean isMultiIncoming(FlowElement flowElement) {
        if (flowElement instanceof FlowNode) {
            return ((FlowNode) flowElement).getIncomingFlows().size() > 1;
        }
        return false;
    }

    public static boolean isMultiOutgoing(FlowElement flowElement) {
        if (flowElement instanceof FlowNode) {
            return ((FlowNode) flowElement).getOutgoingFlows().size() > 1;
        }
        return false;
    }

    public static FlowNode findLastGateWay(@NonNull BpmnModel bpmnModel,
                                           @NonNull Set<FlowNode> gateways) {
        if (gateways.size() == 1) {
            return gateways.iterator().next();
        }
        for (FlowNode gateway : gateways) {
            Set<FlowElement> traveledElements = new HashSet<>();
            bfs(bpmnModel, gateway.getId(), traveledElements::add);
            if (Sets.intersection(traveledElements, gateways).size() < 2) {
                return gateway;
            }
        }
        return null;
    }

    private static int calculateGatewayForkJoin(@NonNull BpmnModel bpmnModel,
                                                @NonNull LinkedHashSet<FlowNode> gateways) {

        FlowNode lastGateWay = findLastGateWay(bpmnModel, gateways);
        if (lastGateWay == null) {
            return 0;
        }

        ArrayDeque<FlowNode> gatewayDeque = new ArrayDeque<>(gateways);
        int forkJoinCount = gatewayDeque.pop().getOutgoingFlows().size();

        while (!gatewayDeque.isEmpty()) {
            FlowNode gateway = gatewayDeque.pop();
            if (!gateway.equals(lastGateWay)) {
                forkJoinCount += gateway.getOutgoingFlows().size() - gateway.getIncomingFlows().size();
            } else {
                forkJoinCount -= gateway.getIncomingFlows().size();
            }
        }
        if (forkJoinCount > 1) {
            forkJoinCount += lastGateWay.getOutgoingFlows().size();
        }
        return forkJoinCount;
    }

    public static void travel(@NonNull BpmnModel bpmnModel,
                              @NonNull String startNodeId,
                              String stopNodeId,
                              @NonNull Consumer<FlowElement> visitor) {

        var startNode = ((FlowNode) bpmnModel.getFlowElement(startNodeId));
        if (startNode == null) {
            return;
        }
        ArrayDeque<String> deque = new ArrayDeque<>();
        deque.push(startNodeId);

        Set<String> visitedNodeIds = new HashSet<>();
        Set<SequenceFlow> visitedSequences = new HashSet<>();
        LinkedHashSet<FlowNode> gateways = new LinkedHashSet<>();
        // 检查可以遍历到的元素
        Set<FlowElement> reachableElements = new HashSet<>();
        bfs(bpmnModel, startNodeId, reachableElements::add);
        Set<SequenceFlow> reachableSequenceFlows = reachableElements.stream()
                .filter(e -> e instanceof SequenceFlow)
                .map(e -> ((SequenceFlow) e))
                .collect(Collectors.toSet());

        bfsLoop:
        while (!deque.isEmpty()) {

            List<FlowNode> flowNodes = new ArrayList<>();
            for (String nodeId : deque) {
                if (!visitedNodeIds.add(nodeId)) {
                    continue;
                }
                FlowNode flowNode = ((FlowNode) bpmnModel.getFlowElement(nodeId));
                flowNodes.add(flowNode);
                if (flowNode instanceof Gateway || isMultiOutgoing(flowNode) || isMultiIncoming(flowNode)) {
                    gateways.add(flowNode);
                    if (!isMultiOutgoing(flowNode) && Sets.intersection(reachableSequenceFlows, new HashSet<>(flowNode.getIncomingFlows())).size() < 2) {
                        gateways.remove(flowNode);
                    }
                }

            }
            deque.clear();

            int gatewayForkJoinCount = calculateGatewayForkJoin(bpmnModel, gateways);

            Set<SequenceFlow> allGatewayIncomingFlows = gateways.stream()
                    .map(FlowNode::getIncomingFlows)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());

            if (!gateways.isEmpty() && gatewayForkJoinCount <= 0 && visitedSequences.containsAll(
                    Sets.intersection(allGatewayIncomingFlows, reachableSequenceFlows))) {
                gateways.clear();
            }
            // 判断是否需要结束
            boolean stopSearch = flowNodes.stream()
                    .map(BaseElement::getId)
                    .anyMatch(nodeId -> nodeId.equals(stopNodeId));
            for (FlowNode flowNode : flowNodes) {

                if (gateways.contains(flowNode) && !visitedSequences.containsAll(
                        Sets.intersection(new HashSet<>(flowNode.getIncomingFlows()), reachableSequenceFlows))) {
                    visitedNodeIds.remove(flowNode.getId());
                    stopSearch = false;
                    continue;
                }
                if (stopSearch && flowNode.getId().equals(stopNodeId)) {
                    break bfsLoop;
                }
                flowNode.getOutgoingFlows().stream()
                        .map(SequenceFlow::getTargetRef)
                        .filter(Objects::nonNull)
                        .distinct()
                        .forEach(deque::addLast);

                visitor.accept(flowNode);
                flowNode.getOutgoingFlows().forEach(visitor);

                visitedSequences.addAll(flowNode.getOutgoingFlows());
            }
        }

    }


    public static <T> T findPreFlowElement(CommandContext commandContext, FlowNode currentFlowNode, Class<T> elementType) {
        EntityCache entityCache = CommandContextUtil.getEntityCache(commandContext);

        List<ActivityInstanceEntity> activityInstanceEntities = entityCache.findInCache(ActivityInstanceEntity.class);
        Map<String, ActivityInstanceEntity> sequenceFlowMap = activityInstanceEntities.stream()
                .filter(e -> e.getActivityType().equals(BpmnXMLConstants.ELEMENT_SEQUENCE_FLOW))
                .collect(Collectors.toMap(ActivityInstance::getActivityId, Function.identity(), CollectorsUtil.useFirst()));

        Set<SequenceFlow> incomingElements = currentFlowNode.getIncomingFlows().stream()
                .filter(seq -> sequenceFlowMap.containsKey(seq.getId()))
                .collect(Collectors.toSet());

        if (incomingElements.size() != 1) {
            return null;
        }
        SequenceFlow sequenceFlow = incomingElements.iterator().next();
        if (!(sequenceFlow.getTargetFlowElement() instanceof FlowNode)) {
            return null;
        }
        if (elementType.equals(sequenceFlow.getTargetFlowElement().getClass())) {
            return (T) sequenceFlow.getTargetFlowElement();
        }
        return findPreFlowElement(commandContext, ((FlowNode) sequenceFlow.getTargetFlowElement()), elementType);
    }
}
