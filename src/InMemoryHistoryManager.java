import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.practicum.objects.*;

public class InMemoryHistoryManager implements HistoryManager {
    HashMap<Integer, Node> nodeList = new HashMap<>();
    CustomLinkedList customLinkedList = new CustomLinkedList();

    public List<Task> getViewHistory() {
        return customLinkedList.getTasks(nodeList);
    }

    @Override
    public void add(Task task) {
        if (nodeList.containsKey(task.getId())) {
            customLinkedList.removeNode(nodeList.get(task.getId()));
        }
        nodeList.put(task.getId(), customLinkedList.linkLast(task));
    }

    @Override
    public void remove(int id) {
        if (nodeList.containsKey(id)) {
            customLinkedList.removeNode(nodeList.get(id));
            nodeList.remove(id);
        }
    }
}
