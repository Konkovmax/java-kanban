package ru.practicum.konkov.tests;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import ru.practicum.konkov.exceptions.EmptyListException;
import ru.practicum.konkov.exceptions.WrongIdException;
import ru.practicum.konkov.managers.TaskManager;
import ru.practicum.konkov.task.Epic;
import ru.practicum.konkov.task.Status;
import ru.practicum.konkov.task.Subtask;
import ru.practicum.konkov.task.Task;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;


abstract class TaskManagerTest<T extends TaskManager> {
    public T taskManager;

    public abstract T createTaskManager();

    @BeforeEach
    private void updateTaskManager() {
        taskManager = createTaskManager();
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
    }

    // для добавления задач не делал тесты по сценарию: "С пустым списком задач" и "С неверным идентификатором задачи"
    // т.к. в этих методах не важна полнота списка и не используется идентификатор. Это правильно?
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
        WrongIdException ex = assertThrows(
                WrongIdException.class,
                () -> {
                    Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", Status.NEW, 5);
                    taskManager.addSubtask(subtask);
                    final int taskId = subtask.getId();
                    final Task savedTask = taskManager.getSubtaskById(taskId);
                    assertNotNull(savedTask, "Задача не найдена.");
                    assertEquals(subtask, savedTask, "Задачи не совпадают.");
                });
        Assertions.assertEquals("Wrong Id", ex.getMessage());
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
        EmptyListException ex = assertThrows(
                EmptyListException.class,
                () -> {
                    Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW);
                    final int taskId = epic.getId();
                    taskManager.updateTask(epic);
                    final Task savedTask = taskManager.getEpicById(taskId);
                    assertNotNull(savedTask, "Задача не найдена.");
                    assertEquals(epic, savedTask, "Задачи не совпадают.");
                });
        Assertions.assertEquals("List is empty", ex.getMessage());
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
        EmptyListException ex = assertThrows(
                EmptyListException.class,
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
        Assertions.assertEquals("List is empty", ex.getMessage());
    }

    //updateSubtaskWithWrongId() нет смысла, т.к. исключение будет на этапе добавления subtaska

    @Test
    void deleteTask() {
        fillData(taskManager);
        EmptyListException ex = assertThrows(
                EmptyListException.class,
                () -> {
                    taskManager.deleteTask(0);
                    taskManager.getTaskById(0);
                });
        Assertions.assertEquals("List is empty", ex.getMessage());
    }

    @Test
    void deleteEpic() {
        fillData(taskManager);
        EmptyListException ex = assertThrows(
                EmptyListException.class,
                () -> {
                    taskManager.deleteEpic(2);
                    taskManager.getEpicById(2);
                });
        Assertions.assertEquals("List is empty", ex.getMessage());
    }

    @Test
    void deleteSubtask() {
        fillData(taskManager);
        EmptyListException ex = assertThrows(
                EmptyListException.class,
                () -> {
                    taskManager.deleteSubtask(5);
                    taskManager.getSubtaskById(5);
                });
        Assertions.assertEquals("List is empty", ex.getMessage());
    }

    // для проверки отсутствия задач, нужен либо метод для доступа к спискам, либо изменить права доступа к спискам
    // но мне показалось странно менять систему ради тестов. По идее это тесты для программы, а не наоборот?
// или надо дописать?
    @Test
    void removeAll() {
    }

    // методы get...ById c корректными данными тестируются в тестах на добавление задач
    @Test
    void getTaskByWrongId() {

        EmptyListException ex = assertThrows(
                EmptyListException.class,
                () -> {
                    Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
                    taskManager.addTask(task);
                    final int taskId = task.getId() + 3;
                    taskManager.getTaskById(taskId);
                });
        Assertions.assertEquals("List is empty", ex.getMessage());
    }

    @Test
    void getSubtaskById() {
        WrongIdException ex = assertThrows(
                WrongIdException.class,
                () -> {
                    Subtask subtask = new Subtask("Test addNewEpic", "Test addNewEpic description", Status.NEW, 1);
                    taskManager.addSubtask(subtask);
                    final int taskId = subtask.getId() + 3;
                    taskManager.getSubtaskById(taskId);
                });
        Assertions.assertEquals("Wrong Id", ex.getMessage());
    }

    @Test
    void getEpicById() {
        EmptyListException ex = assertThrows(
                EmptyListException.class,
                () -> {
                    Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW);
                    taskManager.addEpic(epic);
                    final int taskId = epic.getId() + 3;
                    taskManager.getEpicById(taskId);
                });
        Assertions.assertEquals("List is empty", ex.getMessage());

    }

    //тестов на печать в теории не было - это то что на гуглил
    private ByteArrayOutputStream output = new ByteArrayOutputStream();

    @Test
    public void printTasksStandard() {
        System.setOut(new PrintStream(output));
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        taskManager.addTask(task);
        taskManager.printTasks();
        Assertions.assertEquals("Task: " + task + "\r\n", output.toString());
        System.setOut(null);
    }

    @Test
    public void printTasksEmptyList() {
        System.setOut(new PrintStream(output));
        taskManager.printTasks();
        Assertions.assertEquals("", output.toString());
        System.setOut(null);
    }

    @Test
    public void printEpicsStandard() {
        System.setOut(new PrintStream(output));
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW);
        taskManager.addEpic(epic);
        taskManager.printEpics();
        Assertions.assertEquals("Epic: " + epic + "\r\n" + "  Subtasks list:" + "\r\n" + " " + "\r\n", output.toString());
        System.setOut(null);
    }

    @Test
    public void printEpicEmptyList() {
        System.setOut(new PrintStream(output));
        taskManager.printEpics();
        Assertions.assertEquals("", output.toString());
        System.setOut(null);
    }

    @Test
    public void printSubtasksStandard() {
        System.setOut(new PrintStream(output));
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", Status.NEW, 0);
        taskManager.addSubtask(subtask);
        taskManager.printSubtasks();
        Assertions.assertEquals("Subtask: " + subtask + "\r\n", output.toString());
        System.setOut(null);
    }

    @Test
    public void printSubtasksEmptyList() {
        System.setOut(new PrintStream(output));
        taskManager.printSubtasks();
        Assertions.assertEquals("", output.toString());
        System.setOut(null);
    }

    @Test
    public void printEpicSubtasksStandard() {
        System.setOut(new PrintStream(output));
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", Status.NEW, 0);
        taskManager.addSubtask(subtask);
        taskManager.printEpicSubtasks(epic);
        Assertions.assertEquals("Subtask: " + subtask + "\r\n", output.toString());
        System.setOut(null);
    }

    @Test
    public void printEpicSubtasksEmptyList() {
        System.setOut(new PrintStream(output));
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", Status.NEW);
        taskManager.addEpic(epic);
        taskManager.printEpicSubtasks(epic);
        Assertions.assertEquals("", output.toString());
        System.setOut(null);
    }

    @Test
    void printViewHistoryStandard() {
        System.setOut(new PrintStream(output));
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        taskManager.addTask(task);
        final int taskId = task.getId();
        taskManager.getTaskById(taskId);
        taskManager.printViewHistory();
        Assertions.assertEquals(" Viewed tasks from latest to oldest" + "\r\n" + task + "\r\n", output.toString());
        System.setOut(null);
    }

    @Test
    void printViewHistoryEmpty() {
        System.setOut(new PrintStream(output));
        taskManager.printViewHistory();
        Assertions.assertEquals(" Viewed tasks from latest to oldest"+ "\r\n", output.toString());
        System.setOut(null);
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
                   // final Task savedTask = taskManager.getEpicById(taskId);
       taskManager.getHistory().get(0);

                });
        Assertions.assertEquals("Index 0 out of bounds for length 0", ex.getMessage());
    }
}

