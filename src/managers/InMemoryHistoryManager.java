package managers;

import model.Epic;
import model.Subtask;
import model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> head;
    private Node<Task> tail;
    private final Map<Integer, Node<Task>> nodeMap = new HashMap<>();

    private void linkLast(Task task) {
        Node<Task> newNode = new Node<>(task);
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        nodeMap.put(task.getId(), newNode);
    }

    private void removeNode(Node<Task> node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        nodeMap.remove(node.data.getId());
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> current = head;

        while (current != null) {
            tasks.add(current.data);
            current = current.next;
        }
        return tasks;
    }


    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        int taskId = task.getId();
        if (nodeMap.containsKey(taskId)) {
            removeNode(nodeMap.get(taskId));
        }
        // Создаем копию задачи перед добавлением в историю
        Task taskCopy;
        if (task instanceof Subtask) {
            taskCopy = new Subtask((Subtask) task);
        } else if (task instanceof Epic) {
            taskCopy = new Epic((Epic) task);
        } else {
            taskCopy = new Task(task);
        }
        linkLast(taskCopy);
    }

    @Override
    public void remove(int id) {
        if (nodeMap.containsKey(id)) {
            removeNode(nodeMap.get(id));
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}