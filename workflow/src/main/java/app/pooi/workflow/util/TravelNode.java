package app.pooi.workflow.util;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

@Data
@RequiredArgsConstructor
public class TravelNode<T extends TravelNode<T>> {

    private final String value;

    private Set<T> children = HashSet.newHashSet(0);

    /**
     * find target searchValue in travel node
     *
     * @param root        node root
     * @param searchValue search searchValue
     * @param <T>
     * @return target node
     */
    public static <T extends TravelNode<T>> Optional<T> find(T root, String searchValue) {

        Deque<T> q = new ArrayDeque<>();
        q.push(root);

        while (!q.isEmpty()) {
            T currentNode = q.pop();
            if (currentNode.getValue().equals(searchValue)) {
                return Optional.of(currentNode);
            }

            for (T travelNode : currentNode.getChildren()) {
                q.push((travelNode));
            }
        }

        return Optional.empty();
    }

    public static <T extends TravelNode<T>> Optional<List<T>> findWithPathBFS(T root, String value) {
        // 队列用于BFS遍历
        Queue<T> queue = new LinkedList<>();
        // 记录每个节点的父节点用于回溯路径
        Map<T, T> parentMap = new HashMap<>();
        // 记录已访问节点防止重复处理
        Set<T> visited = new HashSet<>();

        // 初始化
        queue.offer(root);
        parentMap.put(root, null);
        visited.add(root);

        while (!queue.isEmpty()) {
            T currentNode = queue.poll();

            // 检查是否找到目标节点
            if (currentNode.getValue().equals(value)) {
                // 构建并返回路径
                return Optional.of(buildPath(currentNode, parentMap));
            }

            // 遍历子节点
            for (T child : currentNode.getChildren()) {
                if (!visited.contains(child)) {
                    visited.add(child);
                    parentMap.put(child, currentNode);
                    queue.offer(child);
                }
            }
        }

        // 未找到目标节点
        return Optional.empty();
    }

    public static <T extends TravelNode<T>> Optional<List<T>> findWithPathDFSIterative(T root, String value) {
        Deque<T> stack = new ArrayDeque<>();
        Deque<List<T>> pathStack = new ArrayDeque<>();
        Set<T> visited = new HashSet<>();

        stack.push(root);
        pathStack.push(new ArrayList<>(Collections.singletonList(root)));
        visited.add(root);

        while (!stack.isEmpty()) {
            T currentNode = stack.pop();
            List<T> currentPath = pathStack.pop();

            if (currentNode.getValue().equals(value)) {
                return Optional.of(currentPath);
            }

            for (T child : currentNode.getChildren()) {
                if (!visited.contains(child)) {
                    visited.add(child);
                    stack.push(child);

                    List<T> newPath = new ArrayList<>(currentPath);
                    newPath.add(child);
                    pathStack.push(newPath);
                }
            }
        }

        return Optional.empty();
    }

    /**
     * 根据父节点映射表构建从根节点到目标节点的路径
     */
    private static <T extends TravelNode<T>> List<T> buildPath(T targetNode, Map<T, T> parentMap) {
        LinkedList<T> path = new LinkedList<>();
        T current = targetNode;

        // 从目标节点回溯到根节点
        while (current != null) {
            path.addFirst(current);
            current = parentMap.get(current);
        }

        return path;
    }

    /**
     * 检测图中是否存在环（DFS方法）
     *
     * @param root 图的根节点
     * @return true如果存在环，false否则
     */
    public static <T extends TravelNode<T>> boolean hasCycle(T root) {
        // 使用两个集合：
        // visited - 记录所有已访问过的节点（避免重复处理）
        // recursionStack - 记录当前DFS路径上的节点（用于检测环）
        Set<T> visited = new HashSet<>();
        Set<T> recursionStack = new HashSet<>();

        return hasCycleHelper(root, visited, recursionStack);
    }

    /**
     * 获取所有叶子节点及其路径
     *
     * @param root 根节点
     * @return 叶子节点路径列表
     * @throws IllegalArgumentException 如果图中存在环
     */
    public static <T extends TravelNode<T>> List<List<T>> getAllLeafPaths(@NonNull T root) {
        // 先检测环
        if (hasCycle(root)) {
            throw new IllegalArgumentException("Diagram has cycle");
        }

        List<List<T>> leafPaths = new ArrayList<>();
        Deque<Pair<T, List<T>>> stack = new ArrayDeque<>();

        // 初始状态：根节点及其路径
        stack.push(Pair.of(root, Collections.singletonList(root)));

        while (!stack.isEmpty()) {
            Pair<T, List<T>> current = stack.pop();
            T node = current.getKey();
            List<T> path = current.getValue();

            if (node.getChildren().isEmpty()) {
                // 找到叶子节点，保存路径
                leafPaths.add(path);
            } else {
                // 处理子节点
                for (T child : node.getChildren()) {
                    // 创建新路径（复制原路径并添加当前子节点）
                    List<T> newPath = new ArrayList<>(path);
                    newPath.add(child);
                    stack.push(Pair.of(child, newPath));
                }
            }
        }

        return leafPaths;
    }

    private static <T extends TravelNode<T>> boolean hasCycleHelper(
            T node,
            Set<T> visited,
            Set<T> recursionStack) {

        // 如果当前节点已经在递归栈中，说明发现了环
        if (recursionStack.contains(node)) {
            return true;
        }

        // 如果已经访问过且不在当前路径中，说明这部分已经检查过无环
        if (visited.contains(node)) {
            return false;
        }

        // 标记当前节点为已访问，并加入当前路径
        visited.add(node);
        recursionStack.add(node);

        // 递归检查所有子节点
        for (T child : node.getChildren()) {
            if (hasCycleHelper(child, visited, recursionStack)) {
                return true;
            }
        }

        // 回溯：从当前路径中移除当前节点
        recursionStack.remove(node);
        return false;
    }

    public boolean addChild(T child) {
        return this.children.add(child);
    }

    public Optional<List<T>> findWithPathBFS(String searchValue) {
        return findWithPathBFS((T) this, searchValue);
    }

    public Optional<List<T>> findWithPathDFSIterative(String searchValue) {
        return findWithPathDFSIterative((T) this, searchValue);
    }

    public List<List<T>> getLeafNodes() {
        return getAllLeafPaths((T) this);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TravelNode<?> that)) return false;
        return Objects.equals(value, that.value);
    }
}
