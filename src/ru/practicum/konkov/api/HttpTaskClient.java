package ru.practicum.konkov.api;

import com.google.gson.Gson;
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


public class HttpTaskClient {


    static FileBackedTasksManager fileTasksManager = new FileBackedTasksManager("backedtasks.csv");
    private static Gson gson = new Gson();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;


    static private HttpTaskServer httpTaskServer;
    static private HttpClient taskClient;

    public static void main(String[] args) throws IOException {
        String url = "http://localhost:8080/tasks/";

        httpTaskServer = new HttpTaskServer();
        httpTaskServer.startServer();
        taskClient = HttpClient.newHttpClient();


        fillData(fileTasksManager);
        URI uri = URI.create(url + "task/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            final HttpResponse<String> response = taskClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("ok");
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }


        } catch (IOException | NullPointerException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
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

}

