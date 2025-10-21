/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package app.pooi.workflow.domain.model;

import app.pooi.workflow.domain.model.workflow.agency.TaskDelegateNode;
import app.pooi.workflow.util.TravelNode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class ApprovalDelegateNodeTest {

    @Test
    void findLeafNodes() {

        // 构建测试树
        TravelNode root = new TravelNode<>("A");
        TravelNode nodeB = new TravelNode<>("B");
        TravelNode nodeC = new TravelNode<>("C");
        TravelNode nodeD = new TravelNode<>("D");
        TravelNode nodeE = new TravelNode<>("E");

        root.addChild(nodeB);
        root.addChild(nodeC);
        nodeB.addChild(nodeD);
        nodeC.addChild(nodeE);

        // 获取所有叶子路径
        List<List<TravelNode>> leafPaths = TravelNode.getAllLeafPaths(root);

        // 打印结果
        for (List<TravelNode> path : leafPaths) {
            System.out.println("叶子节点: " + path.getLast().getValue());
            System.out.println("路径: " +
                    path.stream().map(TravelNode::getValue).collect(Collectors.joining(" → ")));
        }
/*
输出:
叶子节点: D
路径: A → B → D
叶子节点: E
路径: A → C → E
*/
    }

    @Test
    void testFind() {
        TaskDelegateNode a = new TaskDelegateNode("A");
        TaskDelegateNode b = new TaskDelegateNode("B");
        TaskDelegateNode c = new TaskDelegateNode("C");
        a.getChildren().add(b);
        b.getChildren().add(c);

        Optional<TaskDelegateNode> optional = TaskDelegateNode.find(a, "C");
        Assertions.assertThat(optional).isPresent();
    }

    @Test
    void testFindCycle() {

        TaskDelegateNode a = new TaskDelegateNode("A");
        TaskDelegateNode b = new TaskDelegateNode("B");
        TaskDelegateNode c = new TaskDelegateNode("C");
        a.getChildren().add(b);
        b.getChildren().add(c);

        boolean hasCycle = TaskDelegateNode.hasCycle(a);
        // a -> b -> c
        Assertions.assertThat(hasCycle).isFalse();

        c.addChild(a);
        hasCycle = TaskDelegateNode.hasCycle(a);
        // has cycle
        Assertions.assertThat(hasCycle).isTrue();
    }
}