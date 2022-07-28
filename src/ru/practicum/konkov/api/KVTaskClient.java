package ru.practicum.konkov.api;

import ru.practicum.konkov.exceptions.APIException;
import ru.practicum.konkov.exceptions.NotFoundException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String url;

    private String API_TOKEN;

    public KVTaskClient(String url) {
        this.url = url;

        URI uri = URI.create(url + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                API_TOKEN = response.body();
                System.out.println(API_TOKEN);
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }


        } catch (IOException | NullPointerException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new APIException("registration failed");
        }
    }

    public void put(String key, String json) {
        URI uri = URI.create(url + "/save/" + key + "?API_TOKEN=DEBUG");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {

                System.out.println("object saved");
            } else {
                throw new APIException("object not saved");
            }
        } catch (IOException | NullPointerException | InterruptedException e) { // обрабатываем ошибки отправки запроса

        }
    }

    public String load(String key) {
        String result = "object not found";
        URI uri = URI.create(url + "/load/" + key + "?API_TOKEN=DEBUG");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                result = response.body();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }


        } catch (IOException | NullPointerException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new APIException("object not loaded");
        }
        return result;
    }

}



