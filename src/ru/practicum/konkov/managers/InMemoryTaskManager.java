package ru.practicum.konkov.managers;

import ru.practicum.konkov.exceptions.EmptyListException;
import ru.practicum.konkov.exceptions.NotFoundException;
import ru.practicum.konkov.task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {


    private static final int INTERVALS_OF_15MINUTES_PER_YEAR = 35040;
    public static ZoneId zone = ZoneId.of("Europe/Moscow");
    public static final ZonedDateTime START_DAY_OF_TASK_LIST = ZonedDateTime.of(LocalDateTime.of(2022, 5, 1, 0, 0), zone);

    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected HistoryManager history = Managers.getDefaultHistory();
    protected Map<Integer, Boolean> busyIntervals = new HashMap<>();

    private int id = 0;

    private Comparator<Task> comparator = (t1, t2) -> {
        if (t1.getStartTime() != null && t2.getStartTime() != null) {
            return t1.getStartTime().compareTo(t2.getStartTime());
        } else if (t1.getStartTime() == null && t2.getStartTime() == null) {
            return t1.getId() - t2.getId();
        } else if (t1.getStartTime() != null && t2.getStartTime() == null) {
            return -1;
        } else {
            return 1;
        }
    };

    public Set<Task> sortedTasks = new TreeSet<>(comparator);

    public void fillBusyIntervals() {
        for (int i = 0; i < INTERVALS_OF_15MINUTES_PER_YEAR; i++) {
            busyIntervals.put(i, false);
        }
    }

    @Override
    public void addTask(Task task) {
        task.setId(id);
        tasks.put(id, task);
        checkTimeCrossing(task);
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
            checkTimeCrossing(subtask);
            epics.get(epicId).getSubtasks().add(subtask);
            epics.get(epicId).setStatus(calculateEpicStatus(epicId));
            epics.get(epicId).calculateDuration();
            sortedTasks.add(subtask);
            generateNewId();
        } catch (NullPointerException e) {
            throw new NotFoundException("No epic found by id");
        }
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        checkTimeCrossing(task);
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
        checkTimeCrossing(subtask);
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
        Task targetTask = null;
        if (subtasks.containsKey(id)) {
            targetTask = subtasks.get(id);
            history.add(targetTask);
            return targetTask;
        } else {
            throw new NotFoundException("Subtask not found");
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task targetTask = null;
        if (tasks.containsKey(id)) {
            targetTask = tasks.get(id);
            history.add(targetTask);
            return targetTask;
        } else {
            throw new NotFoundException("Task not found");
        }
    }

    @Override
    public Task getEpicById(int id) {
        Task targetTask = null;
        if (epics.containsKey(id)) {
            targetTask = epics.get(id);
            history.add(targetTask);
            return targetTask;
        } else {
            throw new NotFoundException("Epics not found");
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

    @Override
    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public Map<Integer, Epic> getEpics() {
        return epics;
    }


    public void checkTimeCrossingBasic(Task newTask) {
        if (!sortedTasks.isEmpty() && newTask.getStartTime() != null) {
            for (Task task : sortedTasks) {
                if ((newTask.getStartTime().isAfter(task.getStartTime()) && newTask.getStartTime().isBefore(task.getEndTime())) ||
                        newTask.getStartTime().isEqual(task.getStartTime())) {
                    newTask.setStartTime(task.getEndTime().plusMinutes(15));
                    break;
                }
                if (newTask.getEndTime().isAfter(task.getStartTime()) && newTask.getEndTime().isBefore(task.getEndTime())) {
                    newTask.setStartTime(task.getEndTime().plusMinutes(15));
                }
            }
        }
    }

    public void checkTimeCrossing(Task newTask) {
        if (newTask.getStartTime() != null) {
            boolean timeCrossingEliminated = true;
            while (timeCrossingEliminated) {
                int interval = (int) Duration.between(START_DAY_OF_TASK_LIST, newTask.getStartTime()).toMinutes() / 15;
                if (busyIntervals.get(interval)) {
                    newTask.setStartTime(newTask.getEndTime().plusMinutes(15));
                } else {
                    timeCrossingEliminated = false;
                    busyIntervals.put(interval, true);
                }
            }
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

    @Override
    public Set<Task> getPrioritizedTasks() {

        return sortedTasks;
    }

    private void generateNewId() {
        id++;
    }

    public void generateNewId(int id) {
        this.id = id + 1;
    }

}
