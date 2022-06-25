package ru.practicum.konkov.managers;

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

    public FileBackedTasksManager(String fileName) {
        file = new File("." + File.separator + "resources" + File.separator, fileName);
    }

    public static void main(String[] args) {
        FileBackedTasksManager fileTasksManager = new FileBackedTasksManager(fileName);
//        fillData(fileTasksManager);
//        fileTasksManager.getTaskById(1);
//        fileTasksManager.getTaskById(0);
//        fileTasksManager.getEpicById(2);
//        fileTasksManager.getSubtaskById(5);

        System.out.println(" Tasks list:");
        loadFromFile(file).printTasks();
        System.out.println(" Epics list:");
        loadFromFile(file).printEpics();
        loadFromFile(file).printViewHistory();

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

    public void save() throws ManagerSaveException{
        String header = "id,type,name,status,description,epic";
        try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
            //todo check comments
            fileWriter.write(header + "\n");
            for (Task task : tasks.values()) {
                fileWriter.write(taskToString(task) + "\n");
            }
            for (Task task : epics.values()) {
                fileWriter.write(taskToString(task) + "\n");
            }
            for (Subtask subtask : subtasks.values()) {
                fileWriter.write(subtaskToString(subtask) + "\n");
            }
            fileWriter.write("\n");
             fileWriter.write(historyToString(history));

// todo ManagerSaveException
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время записи файла.");
        }

    }//todo format style

    public String taskToString(Task task) {
        return Integer.toString(task.getId()) + "," + getTaskType(task) + "," + task.getName() + ","
                + task.getStatus() + "," + task.getDescription() + ",";
    }

    public String subtaskToString(Subtask task) {
        return Integer.toString(task.getId()) + "," + getTaskType(task) + "," + task.getName() + ","
                + task.getStatus() + "," + task.getDescription() + "," + task.getEpicId();
    }

    public TaskType getTaskType(Task task) {
        if (task.getClass().getName().contains("Task")) {
            return TaskType.TASK;
        } else if (task.getClass().getName().contains("Epic")) {
            return TaskType.EPIC;
        } else
            return TaskType.SUBTASK;

    }

    static public FileBackedTasksManager loadFromFile(File file) { //todo static
        String fileContents = null;
        FileBackedTasksManager manager = new FileBackedTasksManager(fileName);
        try {
            fileContents = Files.readString(file.toPath());
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время чтения файла.");
        }


        String lines[] = fileContents.split("\n");
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
                String lineContents[] = lines[j].split(",");
                switch (lineContents[1]) {
                    case "TASK": {
                        manager.tasks.put(taskFromString(lineContents).getId(), taskFromString(lineContents));
                        break;
                    }
                    case "EPIC": {
                        manager.epics.put(epicFromString(lineContents).getId(), epicFromString(lineContents));
                        break;
                    }
                    case "SUBTASK": {
                       manager.subtasks.put(subtaskFromString(lineContents).getId(), subtaskFromString(lineContents));
                    }
                }
            }

        }
        return manager;
    }

    static public Task taskFromString(String[] lineContents) {// в ТЗ написано, что аргумент строка, но я подумал, что если
        //я уже делю строку на элементы почему бы их и не передать
        Task task = new Task(lineContents[2], lineContents[4], statusFromString(lineContents[3]));
        task.setId(Integer.parseInt(lineContents[0]));
        return task;
    }

   static public Subtask subtaskFromString(String[] lineContents) {
        Subtask subtask = new Subtask(lineContents[2], lineContents[4], statusFromString(lineContents[3]), Integer.parseInt(lineContents[5]));
        subtask.setId(Integer.parseInt(lineContents[0]));
        return subtask;
    }

   static public Epic epicFromString(String[] lineContents) {
        Epic epic = new Epic(lineContents[2], lineContents[4], statusFromString(lineContents[3]));
        epic.setId(Integer.parseInt(lineContents[0]));
        return epic;
    }

    static public Status statusFromString(String value) {
        Status status;
        switch (value) {
            case "DONE": {
                status = Status.DONE;
                break;
            }
            case "IN_PROGRESS": {
                status = Status.IN_PROGRESS;
                break;
            }
            default:
                status = Status.NEW;
        }
        return status;
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        for (String taskId:value.split(",")){
            history.add(Integer.parseInt(taskId));
        }
        return history;
    }

    static String historyToString(HistoryManager manager) {
        Iterator historyIterator = manager.getViewHistory().iterator();
        String history = "";
        for (Task task : manager.getViewHistory()) {
            history = history + Integer.toString(task.getId());
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

    //    void updateTask(Task task);
//
//    void updateEpic(Epic epic);
//
//    void updateSubtask(Subtask subtask);
//
//    void deleteTask(int id);
//
//    void deleteEpic(int id);
//
//    void deleteSubtask(int id);
//
//    void removeAll(); //point 2.2 technical specification of sprint 3
//
    @Override
    public Task getTaskById(int id) {
        Task targetTask = super.getTaskById(id);
        save();
        return targetTask;
    }

//    void printTasks();
//
//    void printEpics();
//
//    void printEpicSubtasks(Epic epic);
//
//    void printSubtasks();
//
//    void printViewHistory();

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