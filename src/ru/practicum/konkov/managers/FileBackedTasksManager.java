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
    static public File file;
    int lastIdFromFile = 0;

    public FileBackedTasksManager(String fileName) {
        file = new File("." + File.separator + "resources" + File.separator, fileName);
    }
    public FileBackedTasksManager(){

    }

    public static void main(String[] args) {
        FileBackedTasksManager fileTasksManager = new FileBackedTasksManager(fileName);
        fillData(fileTasksManager);
        fileTasksManager.getTaskById(1);
        fileTasksManager.getTaskById(0);
        fileTasksManager.getEpicById(3);
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

    public void save() {
        String header = "id,type,name,status,description,startTime,duration,epic";
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
        manager.fillBusyIntervals();
        try {
            fileContents = Files.readString(file.toPath());
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время чтения файла.");
            throw new ManagerReadException(e.getMessage());
        }
        int historyLine = 0;
        //т.к. сам метод прописан в ТЗ я не стал менять сигнатуру, но цикл разбил на 2 поменьше, согласно последнему ревью
        String[] lines = fileContents.split("\n");
        for (int j = 1; j < lines.length; j++) {
            if (lines[j].equals("")) {
                historyLine = j + 1;
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
                        manager.addSubtask(subtask);
                        manager.updateLastId(subtask.getId());
                    }
                }
            }
        }
        if (historyLine != 0) {
            for (int id : historyFromString(lines[historyLine])) {
                if (manager.tasks.containsKey(id)) {
                    manager.history.add(manager.tasks.get(id));
                } else if (manager.epics.containsKey(id)) {
                    manager.history.add(manager.epics.get(id));
                } else if (manager.subtasks.containsKey(id)) {
                    manager.history.add(manager.subtasks.get(id));
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

    protected static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        if (!value.isEmpty()) {
            for (String taskId : value.split(",")) {
                history.add(Integer.parseInt(taskId));
            }
        }
        return history;
    }

    protected static String historyToString(HistoryManager manager) {
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