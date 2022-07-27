package ru.practicum.konkov.managers;

import ru.practicum.konkov.API.HTTPTaskManager;
import ru.practicum.konkov.Main;

public class Managers {

    public static TaskManager getDefault() {
        return new HTTPTaskManager(Main.url);
    }

    static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}