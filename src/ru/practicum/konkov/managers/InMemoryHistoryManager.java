package ru.practicum.konkov.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.practicum.konkov.customcollection.*;
import ru.practicum.konkov.task.Task;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node> indexedHistory = new HashMap<>();
    private CustomLinkedList history = new CustomLinkedList();

    public List<Task> getViewHistory() {
        return history.getTasks(indexedHistory);
    }

    @Override
    public void add(Task task) {
        if (indexedHistory.containsKey(task.getId())) {
            history.removeNode(indexedHistory.get(task.getId()));
        }
        indexedHistory.put(task.getId(), history.linkLast(task));
    }

    @Override
    public void remove(int id) {
        if (indexedHistory.containsKey(id)) {
            history.removeNode(indexedHistory.get(id));
            indexedHistory.remove(id);
        }
    }
}
