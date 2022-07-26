package ru.practicum.konkov.tests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.konkov.API.HttpTaskServer;
import ru.practicum.konkov.API.EpicAdapter;
import ru.practicum.konkov.API.SubtaskAdapter;
import ru.practicum.konkov.API.TaskAdapter;
import ru.practicum.konkov.managers.FileBackedTasksManager;
import ru.practicum.konkov.managers.TaskManager;
import ru.practicum.konkov.task.Epic;
import ru.practicum.konkov.task.Status;
import ru.practicum.konkov.task.Subtask;
import ru.practicum.konkov.task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class HTTPTaskServerTest {
    String url = "http://localhost:8080/tasks/";
    private static final int PORT = 8080;
    //   private final HttpClient client = HttpClient.newHttpClient();
    //  static FileBackedTasksManager fileTasksManager = new FileBackedTasksManager("backedtasks.csv");
    FileBackedTasksManager fileTasksManager;// = new FileBackedTasksManager("backedtasks.csv");
    public static GsonBuilder builder = new GsonBuilder()
            .registerTypeAdapter(Task.class, new TaskAdapter())
            .registerTypeAdapter(Epic.class, new EpicAdapter())
            .registerTypeAdapter(Subtask.class, new SubtaskAdapter());

    Gson gson = builder.create();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    // HttpTaskServer taskServer = new HttpTaskServer();


    private HttpTaskServer httpTaskServer;
    private HttpClient taskClient;

    @BeforeEach
    public void startServers() throws IOException {
        fileTasksManager = new FileBackedTasksManager("backedtasks.csv");

        httpTaskServer = new HttpTaskServer();

        httpTaskServer.startServer();
        taskClient = HttpClient.newHttpClient();
    }

    @AfterEach
    public void stopServer() {
        httpTaskServer.stopServer();
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
        manager.getTaskById(1);
        manager.getTaskById(0);
        //manager.getEpicById(2);
    }

    @Test
    void GETTaskById() {
              Task receivedTask = null;
        fillData(fileTasksManager);
        URI uri = URI.create(url + "task/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            final HttpResponse<String> response = taskClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                receivedTask = gson.fromJson(response.body(), Task.class);
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | NullPointerException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.");
        }
        Assertions.assertEquals(fileTasksManager.getTaskById(1), receivedTask);
    }

    @Test
    void GETSubtaskById() {
              Subtask receivedTask = null;
        fillData(fileTasksManager);
        URI uri = URI.create(url + "subtask/?id=5");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            final HttpResponse<String> response = taskClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                receivedTask = gson.fromJson(response.body(), Subtask.class);
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | NullPointerException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.");
        }
        Assertions.assertEquals(fileTasksManager.getSubtaskById(5), receivedTask);
    }

    @Test
    void GETEpicById() {
              Epic receivedTask = null;
        fillData(fileTasksManager);
        URI uri = URI.create(url + "epic/?id=3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            final HttpResponse<String> response = taskClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                receivedTask = gson.fromJson(response.body(), Epic.class);
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | NullPointerException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.");
        }
        Assertions.assertEquals(fileTasksManager.getEpicById(3).toString(),receivedTask.toString());
    }

    @Test
    void GETHistory() {
             ArrayList<Task> history = new ArrayList<>();
        fillData(fileTasksManager);
//        fileTasksManager.getTaskById(1);
//        fileTasksManager.getTaskById(0);
//        fileTasksManager.getEpicById(2);
        URI uri = URI.create(url + "history/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            final HttpResponse<String> response = taskClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                history = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
                }.getType());
                //history = gson.fromJson(response.body(), Task.class);
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | NullPointerException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.");
        }

        Assertions.assertEquals(fileTasksManager.getHistory(), history);
    }

       @Test
    void GETTasks() {

        HashMap<Integer, Task> receivedTasks = new HashMap<>();
        fillData(fileTasksManager);
        URI uri = URI.create(url + "task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            final HttpResponse<String> response = taskClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                receivedTasks = gson.fromJson(response.body(), new TypeToken<HashMap<Integer,Task>>(){}.getType());
                           } else {
               System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | NullPointerException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.");
        }
                Assertions.assertEquals(fileTasksManager.getTasks(), receivedTasks);
    }
    @Test
    void POSTTask() {
        String received = null;
        //Map<Integer, Task> receivedTasks;
        //fillData(fileTasksManager);
        Task task1 = new Task("0study encapsulation", "without time", Status.NEW);
        //fileTasksManager.addEpic(epic1);
        URI uri = URI.create(url + "task/");
        String json = gson.toJson(task1, Task.class);
        task1.setId(8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            final HttpResponse<String> response = taskClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // receivedTasks = gson.fromJson(response.body(), Task.class);
                received = response.body();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | NullPointerException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.");
        }
        assertEquals(task1.toFileString(), httpTaskServer.fileTasksManager.getTaskById(8).toFileString());
    }

    @Test
    void DeleteTaskById() {
        URI uri = URI.create(url + "task/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        try {
            final HttpResponse<String> response = taskClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                       } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | NullPointerException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.");
        }
        assertEquals(2,httpTaskServer.fileTasksManager.getTasks().size());
    }

    @Test
    void DeleteEpicById() {
        URI uri = URI.create(url + "epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        try {
            final HttpResponse<String> response = taskClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                       } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | NullPointerException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.");
        }
        assertEquals(1,httpTaskServer.fileTasksManager.getEpics().size());
    }

    @Test
    void DeleteSubtaskById() {
        URI uri = URI.create(url + "subtask/?id=4");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        try {
            final HttpResponse<String> response = taskClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                       } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | NullPointerException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.");
        }
        assertEquals(2,httpTaskServer.fileTasksManager.getSubtasks().size());
    }


    @Test
    void DeleteTasks() {
        URI uri = URI.create(url + "task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        try {
            final HttpResponse<String> response = taskClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                       } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | NullPointerException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.");
        }
        assertEquals(httpTaskServer.fileTasksManager.getTasks().size(), 0);
    }

    @Test
    void DeleteEpics() {
        URI uri = URI.create(url + "epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        try {
            final HttpResponse<String> response = taskClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                       } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | NullPointerException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.");
        }
        assertEquals(httpTaskServer.fileTasksManager.getEpics().size(), 0);
    }

    @Test
    void DeleteSubtasks() {
        URI uri = URI.create(url + "subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        try {
            final HttpResponse<String> response = taskClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                       } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | NullPointerException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.");
        }
        assertEquals(httpTaskServer.fileTasksManager.getSubtasks().size(), 0);
    }
}
