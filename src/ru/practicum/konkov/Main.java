package ru.practicum.konkov;

import ru.practicum.konkov.managers.*;
import ru.practicum.konkov.task.*;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        fillData(manager);
        System.out.println(" Tasks list:");
        manager.printTasks();
        System.out.println(" Epics list:");
        manager.printEpics();
        manager.printViewHistory();
        System.out.println("Task id" + 2 + manager.getEpicById(2));
        System.out.println("Task id" + 0 + manager.getTaskById(0));
        System.out.println("Task id" + 5 + manager.getSubtaskById(5));
        System.out.println("Task id" + 3 + manager.getEpicById(3));
        manager.printViewHistory();
        manager.deleteTask(0);
        manager.printViewHistory();
        manager.deleteEpic(3);
        manager.printViewHistory();
    }

    public static void fillData(TaskManager manager) {
        Task task1 = new Task("study java", "to write quality code", Status.IN_PROGRESS);
        Task task2 = new Task("study encapsulation", "to write very well code", Status.NEW);
        manager.addTask(task1);
        manager.addTask(task2);
        Epic epic1 = new Epic("preparation to sprint", "for execution project");
        Epic epic2 = new Epic("checklist for sprint", "to minimize mistakes");
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        Subtask subtask1 = new Subtask("Initialisation in constructor", "", Status.NEW, 3);
        Subtask subtask2 = new Subtask("Line between methods", "", Status.DONE, 3);
        Subtask subtask3 = new Subtask("visibility of variables", "", Status.NEW, 3);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        manager.deleteSubtask(5);
    }

}
