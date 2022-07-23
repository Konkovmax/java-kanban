package ru.practicum.konkov.API;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.practicum.konkov.managers.FileBackedTasksManager;
import ru.practicum.konkov.managers.TaskManager;
import ru.practicum.konkov.task.Epic;
import ru.practicum.konkov.task.Status;
import ru.practicum.konkov.task.Subtask;
import ru.practicum.konkov.task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static Gson gson = new Gson();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static TaskManager taskManager;

    static FileBackedTasksManager fileTasksManager = new FileBackedTasksManager("backedtasks.csv");

    public static void main(String[] args) throws IOException {
        fillData(fileTasksManager);
        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        // httpServer.stop();
    }

    public static void fillData(FileBackedTasksManager manager) {
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

    static class TasksHandler implements HttpHandler {


        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = null;
            int code = 404;
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            String query = httpExchange.getRequestURI().getQuery();
            int taskId = 0;
            if (query != null) {
                taskId = Integer.parseInt(query.substring(3));
            }

            switch (method) {
                case "GET": {
                    if (path.endsWith("task/")) {
                        if (taskId != 0) {
                            response = gson.toJson(fileTasksManager.getTaskById(taskId));
                        } else {
                            response = gson.toJson(fileTasksManager.getTasks());
                        }
                    }
                    if (path.endsWith("epic/")) {
                        if (taskId != 0) {
                            if (path.endsWith("subtask/epic/")) {
                                response = gson.toJson(fileTasksManager.getSubtaskById(taskId));
                            } else {
                                response = gson.toJson(fileTasksManager.getEpicById(taskId));
                            }
                        } else {
                            response = gson.toJson(fileTasksManager.getEpics());
                        }
                    }
                    if (path.endsWith("subtask/")) {
                        if (taskId != 0) {
                            response = gson.toJson(fileTasksManager.getSubtaskById(taskId));
                        } else {
                            response = gson.toJson(fileTasksManager.getSubtasks());
                        }
                    } if (path.endsWith("tasks/")) {
                            response = gson.toJson(fileTasksManager.getPrioritizedTasks());

                    }
                    if (path.endsWith("history")) {
                        response = gson.toJson(fileTasksManager.getHistory());
                    }

                    code = 200;
                    break;
                }
                case "POST": {
                    InputStream inputStream = httpExchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    if (path.endsWith("task/")) {
                        fileTasksManager.addTask(gson.fromJson(body, Task.class));
                        response = "Task added";

                    }
                    if (path.endsWith("epic/")) {
                        fileTasksManager.addEpic(gson.fromJson(body, Epic.class));
                        response = "Epic added";
                    }
                    if (path.endsWith("subtask/")) {
                        fileTasksManager.addSubtask(gson.fromJson(body, Subtask.class));
                        response = "Subtask added";
                    }
                    code = 200;
                    break;
                }
                case "DELETE": {

                    if (taskId == 0) {
                        fileTasksManager.removeAll();
                    } else {
                        if (path.endsWith("task/")) {
                            fileTasksManager.deleteTask(taskId);
                            response = "Task deleted";
                        }
                        if (path.endsWith("epic/")) {
                            fileTasksManager.deleteEpic(taskId);
                            response = "Epic deleted";
                        }
                        if (path.endsWith("subtask/")) {
                            fileTasksManager.deleteSubtask(taskId);
                            response = "Subtask deleted";
                        }
                    }
                    code = 200;
                }

                default:
                    response = "Некорректный метод!";


            }
            httpExchange.sendResponseHeaders(code, 0);
            try (
                    OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            httpExchange.close();
        }
    }
}
