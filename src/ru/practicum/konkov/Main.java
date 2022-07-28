package ru.practicum.konkov;

import ru.practicum.konkov.api.HTTPTaskManager;
import ru.practicum.konkov.api.KVServer;
import ru.practicum.konkov.managers.*;
import ru.practicum.konkov.task.*;

import java.io.IOException;

public class Main {
    public static String url = "http://localhost:8078";
    private static String key = "test_key";

    public static void main(String[] args) throws IOException {
       KVServer server =  new KVServer();
       server.start();//000
       TaskManager manager = Managers.getDefault();
        fillData(manager);
        TaskManager httpTaskManager = HTTPTaskManager.load();
        System.out.println(" Tasks list:");
        httpTaskManager.printTasks();
        System.out.println(" Epics list:");
        httpTaskManager.printEpics();
        System.out.println("\n\n Sorted Tasks:");
        httpTaskManager.printSortedTasks();
        server.stop();
    }

    public static void fillData(TaskManager manager) {
        manager.fillBusyIntervals();
        Task task1 = new Task("1study java", "to write quality code", Status.IN_PROGRESS, "01.07.22 10:00", 60);
        Task task2 = new Task("2study encapsulation", "to write very well code", Status.NEW, "01.07.22 10:00", 60);
        manager.addTask(task1);
        manager.addTask(task2);
        Epic epic1 = new Epic("preparation to sprint", "for execution project");
        Epic epic2 = new Epic("checklist for sprint", "to minimize mistakes");
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        Subtask subtask1 = new Subtask("3Initialisation in constructor", "", Status.NEW, 3, "21.05.22 10:00", 60);
        Subtask subtask2 = new Subtask("4Line between methods", "", Status.IN_PROGRESS, 3, "01.05.22 10:00", 60);
        Subtask subtask3 = new Subtask("5visibility of variables", "", Status.NEW, 3, "11.05.22 10:00", 60);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        task2 = new Task("0study encapsulation", "without time", Status.NEW);
        manager.addTask(task2);

    }

}
