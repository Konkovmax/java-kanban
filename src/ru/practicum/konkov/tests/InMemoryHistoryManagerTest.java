package ru.practicum.konkov.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.konkov.customcollection.CustomLinkedList;
import ru.practicum.konkov.customcollection.Node;
import ru.practicum.konkov.managers.InMemoryHistoryManager;
import ru.practicum.konkov.managers.InMemoryTaskManager;
import ru.practicum.konkov.managers.TaskManager;
import ru.practicum.konkov.task.Epic;
import ru.practicum.konkov.task.Status;
import ru.practicum.konkov.task.Subtask;
import ru.practicum.konkov.task.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    InMemoryTaskManager taskManager = new InMemoryTaskManager();
    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    Task task = new Task("study java", "to write quality code", Status.IN_PROGRESS);

    public static void fillData(TaskManager manager) {
        Task task1 = new Task("study java", "to write quality code", Status.IN_PROGRESS);
        Task task2 = new Task("study encapsulation", "to write very well code", Status.NEW);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.getTaskById(0);
        manager.getTaskById(1);
        Epic epic1 = new Epic("preparation to sprint", "for execution project");
        Epic epic2 = new Epic("checklist for sprint", "to minimize mistakes");
        manager.addEpic(epic1);
        manager.getEpicById(2);
        manager.addEpic(epic2);
        manager.getEpicById(3);
        Subtask subtask1 = new Subtask("Initialisation in constructor", "", Status.NEW, 3);
        Subtask subtask2 = new Subtask("Line between methods", "", Status.DONE, 3);
        Subtask subtask3 = new Subtask("visibility of variables", "", Status.NEW, 3);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        manager.getSubtaskById(4);
        manager.getSubtaskById(5);
        manager.getSubtaskById(6);
    }
//проверка работы истории с дубликатами в тесте на добавление дубликата
    @Test
    void getViewHistoryStandard() {
fillData(taskManager);
       //historyManager.add(task);
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(7, history.size(), "История не пустая.");
    }

    @Test
    void getViewHistoryEmptyList() {
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(0, history.size(), "История не пустая.");
    }

    @Test
    void addStandard() {
        historyManager.add(task);
        final List<Task> history = historyManager.getViewHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void addDouble() {
        fillData(taskManager);

        taskManager.getTaskById(1);
                final List<Task> history = taskManager.getHistory();

                assertNotNull(history, "История не пустая.");
        assertEquals(7, history.size(), "История не пустая.");
    }

    @Test
    void removeFromStart() {
        fillData(taskManager);
        taskManager.deleteTask(0);
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(6, history.size(), "История не пустая.");
    }
    @Test
    void removeFromMiddle() {
        fillData(taskManager);
        taskManager.deleteEpic(3);
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая.");
    }
    @Test
    void removeFromEnd() {
        fillData(taskManager);
        taskManager.deleteSubtask(6);
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(6, history.size(), "История не пустая.");
    }
    @Test
    void removeFromEmpty() {
        historyManager.remove(1);
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(0, history.size(), "История не пустая.");
    }
    @Test
    void removeWithDouble() {
        fillData(taskManager);
        taskManager.addTask(task);
        taskManager.deleteTask(0);
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(6, history.size(), "История не пустая.");
    }
}