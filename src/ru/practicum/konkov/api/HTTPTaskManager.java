package ru.practicum.konkov.api;

import ru.practicum.konkov.managers.FileBackedTasksManager;
import ru.practicum.konkov.managers.TaskManager;
import ru.practicum.konkov.task.Epic;
import ru.practicum.konkov.task.Subtask;
import ru.practicum.konkov.task.Task;

public class HTTPTaskManager extends FileBackedTasksManager implements TaskManager {
    private static final String KEY = "test_key";

    public static KVTaskClient taskClient;

    private static String url;

    // не решил как лучше использовать эту переменную из класса родителя или просто здесь заново её объявить
    int lastIdFromFile = 0;

    public HTTPTaskManager(String url) {
        this.url = url;

    }

    @Override
    public void save() {
        if (taskClient == null){
            taskClient = new KVTaskClient(url);
        }
        String header = "id,type,name,status,description,startTime,duration,epic";
        String manager = header + "\n";
        for (Task task : tasks.values()) {
            manager = manager + task.toFileString() + "\n";
        }
        for (Epic epic : epics.values()) {
            manager = manager + epic.toFileString() + "\n";
        }
        for (Subtask subtask : subtasks.values()) {
            manager = manager + subtask.toFileString() + "\n";
        }
        manager = manager + "\n" + super.historyToString(history);
        taskClient.put(KEY, manager);
    }


    public static HTTPTaskManager load() {
        String fileContents = null;
        if (taskClient == null){
            taskClient = new KVTaskClient(url);
        }
        HTTPTaskManager manager = new HTTPTaskManager(url);
        manager.fillBusyIntervals();
        fileContents = taskClient.load(KEY);

        int historyLine = 0;
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

}
