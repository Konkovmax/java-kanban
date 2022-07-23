package ru.practicum.konkov.managers;

import ru.practicum.konkov.API.HTTPTaskManager;
import ru.practicum.konkov.Main;
//import ru.practicum.konkov.

public class Managers {

    public static TaskManager getDefault() {
        return new HTTPTaskManager(Main.url);
        //return new InMemoryTaskManager();
    }

    static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}