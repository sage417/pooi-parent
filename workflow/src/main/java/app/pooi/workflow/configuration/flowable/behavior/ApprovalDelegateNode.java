/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package app.pooi.workflow.configuration.flowable.behavior;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.*;

@Accessors(chain = true)
@Getter
class ApprovalDelegateNode {

    private String current;

    private Set<ApprovalDelegateNode> incoming;

    private Set<ApprovalDelegateNode> outgoing;

    private ApprovalDelegateNode() {
    }

    public ApprovalDelegateNode(String current) {
        this.current = current;
        this.outgoing = new HashSet<>();
    }

    public ApprovalDelegateNode addParent(ApprovalDelegateNode child) {
        this.incoming.add(child);
        return this;
    }

    public ApprovalDelegateNode removeParent(ApprovalDelegateNode child) {
        this.incoming.remove(child);
        return this;
    }

    public ApprovalDelegateNode addChild(ApprovalDelegateNode child) {
        this.outgoing.add(child);
        return this;
    }

    public ApprovalDelegateNode removeChild(ApprovalDelegateNode child) {
        this.outgoing.remove(child);
        return this;
    }

    public static ApprovalDelegateNode ORIGINAL() {
        ApprovalDelegateNode delegateNode = new ApprovalDelegateNode();
        delegateNode.current = "__ORIGINAL__";
        delegateNode.incoming = Collections.emptySet();
        return delegateNode;
    }

    static Optional<ApprovalDelegateNode> find(ApprovalDelegateNode root, String find) {

        Deque<ApprovalDelegateNode> q = new ArrayDeque<>();
        q.push(root);

        while (!q.isEmpty()) {
            ApprovalDelegateNode currentNode = q.pop();
            if (currentNode.getCurrent().equals(find)) {
                return Optional.of(currentNode);
            }

            for (ApprovalDelegateNode approvalDelegateNode : currentNode.getOutgoing()) {
                q.push((approvalDelegateNode));
            }
        }

        return Optional.empty();
    }

    public Set<ApprovalDelegateNode> findLeafNodes() {
        Set<ApprovalDelegateNode> leafNodes = new HashSet<>();
        Set<ApprovalDelegateNode> visited = new HashSet<>();
        Queue<ApprovalDelegateNode> queue = new LinkedList<>();

        queue.offer(this);
        visited.add(this);

        while (!queue.isEmpty()) {
            ApprovalDelegateNode current = queue.poll();

            // 如果没有outgoing连接，则为叶子节点
            if (current.outgoing.isEmpty()) {
                leafNodes.add(current);
            }

            // 继续BFS遍历
            for (ApprovalDelegateNode outNode : current.outgoing) {
                if (!visited.contains(outNode)) {
                    visited.add(outNode);
                    queue.offer(outNode);
                }
            }
        }

        return leafNodes;
    }




    public static ApprovalDelegateNode createNode(String current) {
        ApprovalDelegateNode node = new ApprovalDelegateNode(current);
        node.incoming = new HashSet<>();
        return node;
    }

    public ApprovalDelegateNode copyDownwardsByBFS(Map<ApprovalDelegateNode, ApprovalDelegateNode> copiedNodes) {
        // 使用队列进行BFS
        Queue<ApprovalDelegateNode> queue = new LinkedList<>();
        // 使用Map保存原节点到新节点的映射,避免重复复制

        // 从当前节点开始
        queue.offer(this);
        ApprovalDelegateNode newRoot = createNode(this.current);
        copiedNodes.put(this, newRoot);

        while (!queue.isEmpty()) {
            ApprovalDelegateNode original = queue.poll();
            ApprovalDelegateNode copied = copiedNodes.get(original);

            // 只处理出边(向下)
            for (ApprovalDelegateNode outNode : original.outgoing) {
                ApprovalDelegateNode newOutNode = copiedNodes.get(outNode);
                if (newOutNode == null) {
                    // 如果节点未被复制过,创建新节点
                    newOutNode = createNode(outNode.current);
                    copiedNodes.put(outNode, newOutNode);
                    queue.offer(outNode);
                }
                // 建立新节点间的关系
                copied.outgoing.add(newOutNode);
                newOutNode.incoming.add(copied);
            }
        }

        return newRoot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApprovalDelegateNode that = (ApprovalDelegateNode) o;

        return current.equals(that.current);
    }

    @Override
    public int hashCode() {
        return current.hashCode();
    }


    @Override
    public String toString() {
        return "ApprovalDelegateNode{" +
                "current='" + current + '\'' +
                '}';
    }
}
