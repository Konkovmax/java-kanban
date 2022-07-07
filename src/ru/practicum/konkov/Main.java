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
        System.out.println("\n\n Sorted Tasks:");
        manager.printSortedTasks();
//        manager.printViewHistory();
//        System.out.println("Task id" + 2 + manager.getEpicById(2));
//        System.out.println("Task id" + 0 + manager.getTaskById(0));
//        System.out.println("Task id" + 5 + manager.getSubtaskById(5));
//        System.out.println("Task id" + 3 + manager.getEpicById(3));
//        manager.printViewHistory();
//        manager.deleteTask(0);
//        manager.printViewHistory();
//        manager.deleteEpic(3);
//        manager.printViewHistory();
    }

    public static void fillData(TaskManager manager) {
        Task task1 = new Task("1study java", "to write quality code", Status.IN_PROGRESS,"01.07.22 10:00",60);
        Task task2 = new Task("2study encapsulation", "to write very well code", Status.NEW,"01.06.22 10:00",60);
        manager.addTask(task1);
        manager.addTask(task2);
        Epic epic1 = new Epic("preparation to sprint", "for execution project");
        Epic epic2 = new Epic("checklist for sprint", "to minimize mistakes");
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        Subtask subtask1 = new Subtask("3Initialisation in constructor", "", Status.NEW, 3,"21.05.22 10:00",60);
        Subtask subtask2 = new Subtask("4Line between methods", "", Status.IN_PROGRESS, 3,"01.05.22 10:00",60);
        Subtask subtask3 = new Subtask("5visibility of variables", "", Status.NEW, 3,"11.05.22 10:00",60);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        //manager.deleteSubtask(5);
    }

}
