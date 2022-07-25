package ru.practicum.konkov.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.konkov.API.HTTPTaskManager;
import ru.practicum.konkov.API.KVServer;
import ru.practicum.konkov.Main;
import ru.practicum.konkov.exceptions.EmptyListException;
import ru.practicum.konkov.exceptions.ManagerSaveException;
import ru.practicum.konkov.exceptions.NotFoundException;
import ru.practicum.konkov.managers.FileBackedTasksManager;
import ru.practicum.konkov.managers.TaskManager;
import ru.practicum.konkov.task.Epic;
import ru.practicum.konkov.task.Status;
import ru.practicum.konkov.task.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HTTPTaskManagerTest extends TaskManagerTest {

    // KVServer server =  new KVServer();
    KVServer server;// = new KVServer();

    public HTTPTaskManagerTest() throws IOException {
    //    server = new KVServer();
     //   server.start();
    }

    @Override
    public TaskManager createTaskManager() {
        return new HTTPTaskManager(Main.url);
    }


    HTTPTaskManager httpTasksManager = new HTTPTaskManager("http://localhost:8078");
    @BeforeEach
    void startServer() throws IOException {
    //KVServer
           server = new KVServer();
    server.start();
    }

    @AfterEach
    void stopServer(){
        server.stop();
    }
    @Test
    void saveEpicWithoutSubtasksAndWithoutHistory() throws IOException {
        Epic epic1 = new Epic("checklist for sprint", "to minimize mistakes");
        httpTasksManager.addEpic(epic1);
        HTTPTaskManager manager = httpTasksManager.load();
String fileContents = manager.getEpicById(0).toFileString();
        String expected = "0,EPIC,checklist for sprint,NEW,to minimize mistakes,";
        Assertions.assertEquals(expected, fileContents);

    }

    @Test
    void loadFromFileStandard() throws IOException {
//        KVServer server = new KVServer();
//        server.start();
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
//        server.stop();
    }

    @Test
    void loadFromFileWithoutTasks() throws IOException {
//        KVServer server = new KVServer();
//        server.start();
////        fillData(httpTasksManager);
//        httpTasksManager.removeAll();
        HTTPTaskManager manager = httpTasksManager.load();
        final List<Task> history = manager.getHistory();
        assertTrue(history.isEmpty());
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> {
                    manager.getTaskById(1);
                });
        Assertions.assertEquals("Task not found", ex.getMessage());
        //server.stop();
    }

    @Test
    void loadFromFileWithoutHistory() {
        fillData(httpTasksManager);
        HTTPTaskManager manager = httpTasksManager.load();
        final List<Task> history = manager.getHistory();
        assertTrue(history.isEmpty());
    }
}
