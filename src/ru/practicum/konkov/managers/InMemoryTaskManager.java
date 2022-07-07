package ru.practicum.konkov.managers;

import ru.practicum.konkov.exceptions.EmptyListException;
import ru.practicum.konkov.exceptions.WrongIdException;
import ru.practicum.konkov.task.*;

import java.time.ZoneId;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected HistoryManager history = Managers.getDefaultHistory();
    private int id = 0;
    static public ZoneId zone = ZoneId.of("Europe/Moscow");

    Comparator<Task> comparator = new Comparator<Task>() {
        @Override
        public int compare(Task t1, Task t2) {
            if (t1.getStartTime() != null && t2.getStartTime() != null) {
                return t1.getStartTime().compareTo(t2.getStartTime());
            } else if (t1.getStartTime() == null && t2.getStartTime() == null) {
                return t1.getId() - t2.getId();
            } else if (t1.getStartTime() != null && t2.getStartTime() == null) {
                return 1;
            } else {
                return -1;
            }
        }
    };

    public TreeSet<Task> sortedTasks = new TreeSet<>(comparator);

    @Override
    public void addTask(Task task) {
        task.setId(id);
        tasks.put(id, task);
        sortedTasks.add(task);
        generateNewId();
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(id);
        epics.put(id, epic);
               generateNewId();
    }


    @Override
    public void addSubtask(Subtask subtask) {
        try {
            subtask.setId(id);

            subtasks.put(id, subtask);
            int epicId = subtask.getEpicId();
            epics.get(epicId).getSubtasks().add(subtask);
            epics.get(epicId).setStatus(calculateEpicStatus(epicId));
            epics.get(epicId).calculateDuration();
            sortedTasks.add(subtask);
            generateNewId();
        } catch (NullPointerException e) {
            throw new WrongIdException("Wrong Id");
        }
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        sortedTasks.add(task);
    }

    @Override
    public void updateEpic(Epic epic) {

        epics.put(epic.getId(), epic);
        sortedTasks.add(epic);

    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int epicId = subtasks.get(id).getEpicId();
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).getSubtasks().add(subtask);
        epics.get(subtask.getEpicId()).setStatus(calculateEpicStatus(subtask.getEpicId()));
        sortedTasks.add(subtask);
        epics.get(epicId).calculateDuration();
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        for (Subtask subtask : epics.get(id).getSubtasks()) {
            history.remove(subtask.getId());
        }
        epics.get(id).getSubtasks().clear();
        epics.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        int epicId = subtasks.get(id).getEpicId();
        epics.get(epicId).getSubtasks().remove(subtasks.get(id));
        subtasks.remove(id);
        epics.get(epicId).setStatus(calculateEpicStatus(epicId));
        epics.get(epicId).calculateDuration();
        history.remove(id);

    }

    @Override
    public void removeAll() {//point 2.2 technical specification of sprint 3
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Task getSubtaskById(int id) {
        try {
            Task targetTask = null;
            if (subtasks.containsKey(id)) {
                targetTask = subtasks.get(id);
            }

            history.add(targetTask);
            return targetTask;
        } catch (NullPointerException e) {
            throw new EmptyListException("List is empty");
        }
    }

    @Override
    public Task getTaskById(int id) {
        try {
            Task targetTask = null;
            if (tasks.containsKey(id)) {
                targetTask = tasks.get(id);
            }
            history.add(targetTask);
            return targetTask;
        } catch (NullPointerException e) {
            throw new EmptyListException("List is empty");
        }
    }

    @Override
    public Task getEpicById(int id) {
        try {
            Task targetTask = null;
            if (epics.containsKey(id)) {
                targetTask = epics.get(id);
            }
            history.add(targetTask);
            return targetTask;
        } catch (NullPointerException e) {
            throw new EmptyListException("List is empty");
        }
    }


    @Override
    public void printTasks() {
        for (Task task : tasks.values()) {
            System.out.println("Task: " + task);
            history.add(task);
        }
    }

    @Override
    public void printEpics() {
        for (Epic epic : epics.values()) {
            System.out.println("Epic: " + epic);
            history.add(epic);
            System.out.println("  Subtasks list:");
            printEpicSubtasks(epic);
            System.out.println(" ");
        }
    }

    @Override
    public void printEpicSubtasks(Epic epic) {
        for (Subtask subtask : epic.getSubtasks()) {
            System.out.println("Subtask: " + subtask);
            history.add(subtask);
        }
    }

    @Override
    public void printSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            System.out.println("Subtask: " + subtask);
            history.add(subtask);
        }
    }
@Override
    public void printSortedTasks() {
        for (Task task : sortedTasks) {
            System.out.println("Task: " + task);

        }
    }

    public void checkTimeCrossing(Task newTask){
        for (Task task:sortedTasks){
            if()
        }

    }

    private Status calculateEpicStatus(int epicId) {
        int statusSum = 0;
        int subtaskAmount = 0;
        Status result;
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                subtaskAmount++;
                if (subtask.getStatus() == Status.DONE) {
                    statusSum += 2;
                }
                if (subtask.getStatus() == Status.IN_PROGRESS) {
                    statusSum += 1;
                }
            }
        }
        if (statusSum == 0) {
            result = Status.NEW;
        } else {
            result = (statusSum / subtaskAmount == 2) ? Status.DONE : Status.IN_PROGRESS;
        }
        return result;
    }

    @Override
    public void printViewHistory() {
        System.out.println(" Viewed tasks from latest to oldest");
        for (int i = 0; i < history.getViewHistory().size(); i++) {
            System.out.println(history.getViewHistory().get(i));
        }
    }

    @Override
    public List<Task> getHistory() {
        try {
            return history.getViewHistory();
        } catch (IndexOutOfBoundsException e) {
            throw new EmptyListException("List is empty");
        }
    }

    public TreeSet<Task> getPrioritizedTasks(){
        return sortedTasks;
    }

    private void generateNewId() {
        id++;
    }

    public void generateNewId(int id) {
        this.id = id + 1;
    }


}
