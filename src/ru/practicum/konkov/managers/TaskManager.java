package ru.practicum.konkov.managers;

import ru.practicum.konkov.task.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public interface TaskManager {

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    void removeAll(); //point 2.2 technical specification of sprint 3

    Task getTaskById(int id);

    void printTasks();

    void printEpics();

    void printEpicSubtasks(Epic epic);

    void printSubtasks();

    void printViewHistory();

    Task getSubtaskById(int id);

    Task getEpicById(int id);

    List<Task> getHistory();

    void printSortedTasks();

    Set<Task> getPrioritizedTasks();

    Map<Integer, Task> getTasks();

    Map<Integer, Subtask> getSubtasks();

    Map<Integer, Epic> getEpics();

    void fillBusyIntervals();

}
