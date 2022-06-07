import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.practicum.objects.Node;
import ru.practicum.objects.Task;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> viewHistory = new ArrayList<>();
    private Node tail;
    HashMap<Integer, Node> customLinkedList = new HashMap<>();

    public List<Task> getViewHistory() {
        return viewHistory;
    }

    @Override
    public void add(Task task) {
        if (customLinkedList.containsKey(task.getId())) {
            removeNode(customLinkedList.get(task.getId()));
        }
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail != null) {
            oldTail.next = newNode;
        }
        customLinkedList.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            node.next.prev = null;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            node.prev.next = null;
            tail = node.prev;
        }
    }

    @Override
    public void remove(int id) {
        if (customLinkedList.containsKey(id)) {
            removeNode(customLinkedList.get(id));
            customLinkedList.remove(id);
        }
    }

    public void getTasks() {
        viewHistory.clear();
        Node node = tail;
        while (node != null) {
            if (customLinkedList.containsValue(node)) {
                viewHistory.add(node.data);
            }
            node = node.prev;
        }
    }
}
