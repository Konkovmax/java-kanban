package ru.practicum.konkov.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.practicum.konkov.exceptions.WrongIdException;
import ru.practicum.konkov.managers.TaskManager;
import ru.practicum.konkov.task.Epic;
import ru.practicum.konkov.task.Status;
import ru.practicum.konkov.task.Subtask;
import ru.practicum.konkov.task.Task;

import static org.junit.jupiter.api.Assertions.*;


 abstract class TaskManagerTest<T extends TaskManager> {
    public T taskManager;

   public abstract T createTaskManager();

    @BeforeEach
    private void updateTaskManager() {
        taskManager = createTaskManager();
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
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", Status.NEW,0);
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
    void updateSubtask() {
    }

    @Test
    void deleteTask() {
    }

    @Test
    void deleteEpic() {
    }

    @Test
    void deleteSubtask() {
    }

    @Test
    void removeAll() {
    }

    @Test
    void getTaskById() {
    }

    @Test
    void printTasks() {
    }

    @Test
    void printEpics() {
    }

    @Test
    void printEpicSubtasks() {
    }

    @Test
    void printSubtasks() {
    }

    @Test
    void printViewHistory() {
    }

    @Test
    void getSubtaskById() {
    }

    @Test
    void getEpicById() {
    }

    @Test
    void getHistory() {
    }
}

