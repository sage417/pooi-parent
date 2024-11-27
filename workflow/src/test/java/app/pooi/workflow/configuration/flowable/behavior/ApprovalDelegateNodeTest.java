/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package app.pooi.workflow.configuration.flowable.behavior;

import org.junit.jupiter.api.Test;

import java.util.Set;

class ApprovalDelegateNodeTest {

    @Test
    void findLeafNodes() {

        ApprovalDelegateNode nodeA = ApprovalDelegateNode.createNode("A");
        ApprovalDelegateNode nodeB = ApprovalDelegateNode.createNode("B");
        ApprovalDelegateNode nodeC = ApprovalDelegateNode.createNode("C");
        ApprovalDelegateNode nodeD = ApprovalDelegateNode.createNode("D");
        ApprovalDelegateNode nodeE = ApprovalDelegateNode.createNode("E");


        nodeA.getOutgoing().add(nodeB);
        nodeA.getOutgoing().add(nodeC);
        nodeB.getIncoming().add(nodeA);
        nodeC.getIncoming().add(nodeA);
        nodeB.getOutgoing().add(nodeD);
        nodeD.getIncoming().add(nodeB);
        nodeC.getOutgoing().add(nodeE);
        nodeE.getIncoming().add(nodeC);


        Set<ApprovalDelegateNode> leafNodes = nodeA.findLeafNodes();
        System.out.println("Leaf nodes: " + leafNodes); // 会输出节点D和E
    }
}