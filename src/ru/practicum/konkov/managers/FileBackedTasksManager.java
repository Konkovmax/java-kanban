package ru.practicum.konkov.managers;

import ru.practicum.konkov.exceptions.*;
import ru.practicum.konkov.task.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    static String fileName = "backedtasks.csv";
    static File file;
    int lastIdFromFile = 0;

    public FileBackedTasksManager(String fileName) {
        file = new File("." + File.separator + "resources" + File.separator, fileName);
    }

    public static void main(String[] args) {
        FileBackedTasksManager fileTasksManager = new FileBackedTasksManager(fileName);
        fillData(fileTasksManager);
        fileTasksManager.getTaskById(1);
        fileTasksManager.getTaskById(0);
        fileTasksManager.getEpicById(2);
        fileTasksManager.getSubtaskById(5);
        printFile(file);//в ТЗ про это не сказано, но добавил для наглядности, что файл такой как надо

        FileBackedTasksManager manager = loadFromFile(file);
        System.out.println(" Tasks list:");
        manager.printTasks();
        System.out.println(" Epics list:");
        manager.printEpics();
        manager.printViewHistory();

    }

    public static void fillData(FileBackedTasksManager manager) {
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
    }

    public void save() {
        String header = "id,type,name,status,description,epic";
        try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
            fileWriter.write(header + "\n");
            for (Task task : tasks.values()) {
                fileWriter.write(task.toFileString() + "\n");
            }
            for (Epic epic : epics.values()) {
                fileWriter.write(epic.toFileString() + "\n");
            }
            for (Subtask subtask : subtasks.values()) {
                fileWriter.write(subtask.toFileString() + "\n");
            }
            fileWriter.write("\n");
            fileWriter.write(historyToString(history));
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    static public void printFile(File file) {
        String fileContents = null;
        try {
            fileContents = Files.readString(file.toPath());
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время чтения файла.");
            throw new ManagerReadException(e.getMessage());
        }
        System.out.println(fileContents + "\n\n");
    }

    static public FileBackedTasksManager loadFromFile(File file) {
        String fileContents = null;
        FileBackedTasksManager manager = new FileBackedTasksManager(fileName);
        try {
            fileContents = Files.readString(file.toPath());
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время чтения файла.");
            throw new ManagerReadException(e.getMessage());
        }

        String[] lines = fileContents.split("\n");
        for (int j = 1; j < lines.length; j++) {
            if (lines[j].equals("")) {
                for (int id : historyFromString(lines[j + 1])) {
                    if (manager.tasks.containsKey(id)) {
                        manager.history.add(manager.tasks.get(id));
                    } else if (manager.epics.containsKey(id)) {
                        manager.history.add(manager.epics.get(id));
                    } else if (manager.subtasks.containsKey(id)) {
                        manager.history.add(manager.subtasks.get(id));
                    }
                }
                break;
            } else {
                String[] lineContents = lines[j].split(",");
                switch (lineContents[1]) {
                    case "TASK": {
                        Task task = new Task(lineContents);
                        manager.tasks.put(task.getId(), task);
                        manager.updateLastId(task.getId());
                        break;
                    }
                    case "EPIC": {
                        Epic epic = new Epic(lineContents);
                        manager.epics.put(epic.getId(), epic);
                        manager.updateLastId(epic.getId());
                        break;
                    }
                    case "SUBTASK": {
                        Subtask subtask = new Subtask(lineContents);
                        manager.subtasks.put(subtask.getId(), subtask);
                        manager.updateLastId(subtask.getId());
                    }
                }
            }
        }
        manager.generateNewId(manager.lastIdFromFile);
        return manager;
    }

    public void updateLastId(int id) {
        if (lastIdFromFile < id) {
            lastIdFromFile = id;
        }
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        for (String taskId : value.split(",")) {
            history.add(Integer.parseInt(taskId));
        }
        return history;
    }

    static String historyToString(HistoryManager manager) {
        Iterator historyIterator = manager.getViewHistory().iterator();
        String history = "";
        for (Task task : manager.getViewHistory()) {
            history = history + task.getId();
            historyIterator.next();
            if (historyIterator.hasNext()) {
                history = history + ",";
            }
        }
        return history;
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task targetTask = super.getTaskById(id);
        save();
        return targetTask;
    }

    @Override
    public Task getSubtaskById(int id) {
        Task targetTask = super.getSubtaskById(id);
        save();
        return targetTask;
    }

    @Override
    public Task getEpicById(int id) {
        Task targetTask = super.getEpicById(id);
        save();
        return targetTask;
    }
}