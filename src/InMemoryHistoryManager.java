import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> viewHistory = new ArrayList<>();
    private Node<Task> tail;
    HashMap<Integer, Node<Task>> customLinkedList = new HashMap<>();

    @Override
    public List<Task> getViewHistory() {
        return viewHistory;
    }

    @Override
    public void linkLast(Task task) {
        if (customLinkedList.containsKey(task.getId())) {
            removeNode(customLinkedList.get(task.getId()));
        }
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail != null) {
            oldTail.next = newNode;
        }
        customLinkedList.put(task.getId(), newNode);
    }

    void removeNode(Node<Task> node) {
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

    @Override
    public void getTasks() {
        viewHistory.clear();
        Node<Task> node = tail;
        while (node != null) {
            if (customLinkedList.containsValue(node)) {
                viewHistory.add(node.data);
            }
            node = node.prev;
        }
    }
}
