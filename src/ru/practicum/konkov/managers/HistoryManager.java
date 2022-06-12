package ru.practicum.konkov.managers;

import java.util.List;
import ru.practicum.konkov.task.Task;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getViewHistory();

}
