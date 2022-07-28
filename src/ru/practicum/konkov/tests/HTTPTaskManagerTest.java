package ru.practicum.konkov.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.konkov.api.HTTPTaskManager;
import ru.practicum.konkov.api.KVServer;
import ru.practicum.konkov.Main;
import ru.practicum.konkov.exceptions.APIException;
import ru.practicum.konkov.exceptions.NotFoundException;
import ru.practicum.konkov.managers.TaskManager;
import ru.practicum.konkov.task.Epic;
import ru.practicum.konkov.task.Status;
import ru.practicum.konkov.task.Subtask;
import ru.practicum.konkov.task.Task;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HTTPTaskManagerTest extends TaskManagerTest {

    KVServer server;

    public HTTPTaskManagerTest() throws IOException {
    }

    @Override
    public TaskManager createTaskManager() throws IOException {
        return new HTTPTaskManager(Main.url);
    }

    HTTPTaskManager httpTasksManager = new HTTPTaskManager("http://localhost:8078");


    @BeforeEach
    void startServer() throws IOException {
        server = new KVServer();
        server.start();

    }

    @AfterEach
    void stopServer() {
        server.stop();
    }

    @Test
    void saveEpicWithoutSubtasksAndWithoutHistory() throws IOException {

        Epic epic1 = new Epic("from web checklist for sprint", "to minimize mistakes");

        httpTasksManager.addEpic(epic1);
        HTTPTaskManager manager = httpTasksManager.load();
        String fileContents = manager.getEpicById(0).toFileString();
        String expected = "0,EPIC,from web checklist for sprint,NEW,to minimize mistakes,";
        Assertions.assertEquals(expected, fileContents);

    }

    @Test
    void loadFromFileStandard() throws IOException {
        fillData(httpTasksManager);
        httpTasksManager.getTaskById(1);
        httpTasksManager.getTaskById(0);
        httpTasksManager.getEpicById(2);
        HTTPTaskManager manager = httpTasksManager.load();
        final List<Task> history = manager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая.");
        Task expectedTask = new Task("2study encapsulation", "to write very well code", Status.NEW, "01.07.22 11:15", 60);
        final Task task = manager.getTaskById(1);
        expectedTask.setId(1);
        assertEquals(expectedTask, task);
    }

    @Test
    void loadFromFileWithoutTasks() throws IOException {
        APIException ex = assertThrows(
                APIException.class,
                () -> {
        HTTPTaskManager manager = httpTasksManager.load();
                });
        Assertions.assertEquals("object not loaded", ex.getMessage());
    }

    @Test
    void loadFromFileWithoutHistory() {
        fillData(httpTasksManager);
        HTTPTaskManager manager = httpTasksManager.load();
        final List<Task> history = manager.getHistory();
        assertTrue(history.isEmpty());
    }
}
