package ru.practicum.konkov.managers;

import ru.practicum.konkov.exceptions.EmptyListException;
import ru.practicum.konkov.exceptions.WrongIdException;
import ru.practicum.konkov.task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    //C магическим числом - это я сплоховал, согласен, видимо рассказ про https://currentmillis.com
    // создал впечатление, что число стало менее магическим
    static final private int INTERVALS_NUMBER = 35040;
    static public ZoneId zone = ZoneId.of("Europe/Moscow");
    // по инструкции static final должно быть перед static, но в этом случае zone не видит, поэтому я их
    // в хронологическом порядке переставил
    static public final ZonedDateTime START_DAY_OF_TASK_LIST = ZonedDateTime.of(LocalDateTime.of(2022, 5, 1, 0, 0), zone);

    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected HistoryManager history = Managers.getDefaultHistory();
    protected Map<Integer, Boolean> busyIntervals = new HashMap<>();

    private int id = 0;

    protected Comparator<Task> comparator = (t1, t2) -> {
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

    public TreeSet<Task> sortedTasks = new TreeSet<>(comparator);

    // количество 15 минутных интервалов в году - отсюда https://currentmillis.com/?31536000861
    public void fillBusyIntervals() {
        for (int i = 0; i < INTERVALS_NUMBER; i++) {
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

    //я вижу ситуацию следующим образом, id эпика, к которому относится сабтаск, мы передаём в конструкторе
// сабтаска, соответственно мы можем передать id несуществующего эпика и тогда будет ошибка неправильный id
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
            throw new WrongIdException("Wrong Id");
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

    //тут мне не в чем тебя поправить... полностью согласен с твоими рассуждениями.
    // Исправил. Только сделал WrongIdException, а не NotFoundException, чтобы новое исключение не
    // создавать, т.к. решил это не меняет сути
    @Override
    public Task getSubtaskById(int id) {
        Task targetTask = null;
        if (subtasks.containsKey(id)) {
            targetTask = subtasks.get(id);
            history.add(targetTask);
            return targetTask;
        } else {
            throw new WrongIdException("Subtask not found");
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
            throw new WrongIdException("Task not found");
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
            throw new WrongIdException("Epics not found");
        }
    }


    //начал изобретать какую-то конструкцию типа см. ниже для п. 5. но понял, что для разных типов
    // у меня разные списки и разные сообщения об ошибке. И общего только   history.add(targetTask);
    // а делать через List bp Map (task, subtask, epic) - решил, что перебор...
    // но наверноя я просто не понял идею....
    //    public <T> T getSmthById(int id, T t1){
//          }

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

    //я не делал консольный ввод, поэтому в случае пересечения задач по времени, сообщений не выводится,
// просто время новой задачи сдвигается после окончания уже имеющейся +15 минут
    // Т.к. в доп задании пересечение надо искать вторым способом, то я первый сохранил, потому что неизвестно
    //какой из них будет нужнее потом
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

    // Я вижу два варианта:
    // 1. сначала заполнить всё false, а потом по ключу проверять там true или false
    // 2. ничего не заполнять, а потом просто проверять есть такой ключ в мапе, если есть
    // значит интервал занят, если нет, значит помечаем, что этот интервал занят добавляя в мапу этот номер интервала
    // при этом второй способ выглядит привлекательнее(для меня:) ) но почему-то в ТЗ хотят первый.
    // Вопрос чем плох второй способ?
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

    // просто это кардинальная трансформация - перестать выводить в консоль результаты трудов своих,
    // а просто наблюдать их через тесты... Тесты добавил
    @Override
    public TreeSet<Task> getPrioritizedTasks() {

        return sortedTasks;
    }

    private void generateNewId() {
        id++;
    }

    public void generateNewId(int id) {
        this.id = id + 1;
    }

}
