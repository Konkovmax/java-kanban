package ru.practicum.konkov.managers;

import ru.practicum.konkov.api.HTTPTaskManager;
import ru.practicum.konkov.Main;
import ru.practicum.konkov.api.KVServer;

import java.io.IOException;

public class Managers {

    public static TaskManager getDefault() throws IOException {
        KVServer server = new KVServer();
        server.start();
        return new HTTPTaskManager(Main.url);
    }

    static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}