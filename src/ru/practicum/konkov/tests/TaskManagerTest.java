package ru.practicum.konkov.tests;

import org.junit.jupiter.api.*;

import ru.practicum.konkov.api.KVServer;
import ru.practicum.konkov.exceptions.NotFoundException;
import ru.practicum.konkov.managers.TaskManager;
import ru.practicum.konkov.task.Epic;
import ru.practicum.konkov.task.Status;
import ru.practicum.konkov.task.Subtask;
import ru.practicum.konkov.task.Task;

import java.io.IOException;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.konkov.managers.InMemoryTaskManager.START_DAY_OF_TASK_LIST;


abstract class TaskManagerTest<T extends TaskManager> {
    public T taskManager;

    public abstract T createTaskManager() throws IOException;

    @BeforeEach
    private void updateTaskManager() throws IOException {
        taskManager = createTaskManager();
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

    @Test
    public void addTaskStandard() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        taskManager.addTask(task);
        final int taskId = task.getId();
        final Task savedTask = taskManager.getTaskById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    public void addEpicStandard() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW);
        taskManager.addEpic(epic);
        final int taskId = epic.getId();
        final Task savedTask = taskManager.getEpicById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают.");
    }

    @Test
    void addSubtaskStandard() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", Status.NEW, 0);
        taskManager.addSubtask(subtask);
        final int taskId = subtask.getId();
        final Task savedTask = taskManager.getSubtaskById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(subtask, savedTask, "Задачи не совпадают.");
    }

    @Test
    void addSubtaskWithWrongId() {
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> {
                    Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", Status.NEW, 5);
                    taskManager.addSubtask(subtask);
                    final int taskId = subtask.getId();
                    final Task savedTask = taskManager.getSubtaskById(taskId);
                    assertNotNull(savedTask, "Задача не найдена.");
                    assertEquals(subtask, savedTask, "Задачи не совпадают.");
                });
        Assertions.assertEquals("No epic found by id", ex.getMessage());
    }

    @Test
    void updateTaskStandard() {
        Task task = new Task("Test updateTask", "Test updateTask description", Status.NEW);
        taskManager.addTask(task);
        final int taskId = task.getId();
        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task);
        final Task savedTask = taskManager.getTaskById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void updateTaskToEmptyList() {
        Task task = new Task("Test updateTask", "Test updateTask description", Status.NEW);
        final int taskId = task.getId();
        taskManager.updateTask(task);
        final Task savedTask = taskManager.getTaskById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void updateEpicStandard() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW);
        taskManager.addEpic(epic);
        final int taskId = epic.getId();
        epic.setName("PROGRESS");
        taskManager.updateTask(epic);
        final Task savedTask = taskManager.getEpicById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают.");
    }

    @Test
    void updateEpicToEmptyList() {
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> {
                    Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW);
                    final int taskId = epic.getId();
                    taskManager.updateTask(epic);
                    final Task savedTask = taskManager.getEpicById(taskId);
                    assertNotNull(savedTask, "Задача не найдена.");
                    assertEquals(epic, savedTask, "Задачи не совпадают.");
                });
        Assertions.assertEquals("Epics not found", ex.getMessage());
    }

    @Test
    void updateSubtaskStandard() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", Status.NEW, 0);
        taskManager.addSubtask(subtask);
        final int taskId = subtask.getId();
        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(subtask);
        final Task savedTask = taskManager.getSubtaskById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(subtask, savedTask, "Задачи не совпадают.");
    }

    @Test
    void updateSubtaskToEmptyList() {
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> {
                    Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW);
                    taskManager.addEpic(epic);
                    Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", Status.NEW, 0);
                    final int taskId = subtask.getId();
                    taskManager.updateTask(subtask);
                    final Task savedTask = taskManager.getSubtaskById(taskId);
                    assertNotNull(savedTask, "Задача не найдена.");
                    assertEquals(subtask, savedTask, "Задачи не совпадают.");
                });
        Assertions.assertEquals("Subtask not found", ex.getMessage());
    }

    @Test
    void deleteTask() {
        fillData(taskManager);
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> {
                    taskManager.deleteTask(0);
                    taskManager.getTaskById(0);
                });
        Assertions.assertEquals("Task not found", ex.getMessage());
    }

    @Test
    void deleteEpic() {
        fillData(taskManager);
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> {
                    taskManager.deleteEpic(2);
                    taskManager.getEpicById(2);
                });
        Assertions.assertEquals("Epics not found", ex.getMessage());
    }

    @Test
    void deleteSubtask() {
        fillData(taskManager);
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> {
                    taskManager.deleteSubtask(5);
                    taskManager.getSubtaskById(5);
                });
        Assertions.assertEquals("Subtask not found", ex.getMessage());
    }

    @Test
    void removeAll() {
    }

    // методы get...ById c корректными данными тестируются в тестах на добавление задач
    @Test
    void getTaskByWrongId() {

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> {
                    Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
                    taskManager.addTask(task);
                    final int taskId = task.getId() + 3;
                    taskManager.getTaskById(taskId);
                });
        Assertions.assertEquals("Task not found", ex.getMessage());
    }

    @Test
    void getSubtaskById() {
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> {
                    Subtask subtask = new Subtask("Test addNewEpic", "Test addNewEpic description", Status.NEW, 1);
                    taskManager.addSubtask(subtask);
                    final int taskId = subtask.getId() + 3;
                    taskManager.getSubtaskById(taskId);
                });
        Assertions.assertEquals("No epic found by id", ex.getMessage());
    }

    @Test
    void getEpicById() {
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> {
                    Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW);
                    taskManager.addEpic(epic);
                    final int taskId = epic.getId() + 3;
                    taskManager.getEpicById(taskId);
                });
        Assertions.assertEquals("Epics not found", ex.getMessage());

    }

    @Test
    void getHistoryStandard() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        taskManager.addTask(task);
        final int taskId = task.getId();
        taskManager.getTaskById(taskId);
        final Task savedTask = taskManager.getHistory().get(0);
        Assertions.assertEquals(savedTask, task);

    }

    @Test
    void getHistoryEmptyList() {
        IndexOutOfBoundsException ex = assertThrows(
                IndexOutOfBoundsException.class,
                () -> {
                    Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW);
                    final int taskId = epic.getId();
                    taskManager.addTask(epic);

                    taskManager.getHistory().get(0);

                });
        Assertions.assertEquals("Index 0 out of bounds for length 0", ex.getMessage());
    }

    @Test
    void getPrioritizedTasks() {
        fillData(taskManager);
        Boolean isSorted = true;
        ZonedDateTime previos = START_DAY_OF_TASK_LIST;
        for (Task task : taskManager.getPrioritizedTasks()) {
            if (task.getStartTime() != null) {
                if (previos.isAfter(task.getStartTime())) {
                    isSorted = false;
                    break;
                }
                previos = task.getStartTime();
            }
        }
        assertTrue(isSorted, "List isn't sorted");

    }

    @Test
    void getTasks() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        taskManager.addTask(task);
        final int taskId = task.getId();
        final Task savedTask = taskManager.getTasks().get(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

    }

    @Test
    void getSubtasks() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", Status.NEW, 0);
        taskManager.addSubtask(subtask);
        final int taskId = subtask.getId();
        final Task savedTask = taskManager.getSubtasks().get(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(subtask, savedTask, "Задачи не совпадают.");

    }

    @Test
    void getEpics() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW);
        taskManager.addEpic(epic);
        final int taskId = epic.getId();
        final Task savedTask = taskManager.getEpics().get(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают.");

    }
}

