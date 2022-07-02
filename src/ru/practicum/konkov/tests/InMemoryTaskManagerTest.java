package ru.practicum.konkov.tests;

import ru.practicum.konkov.managers.InMemoryTaskManager;
import ru.practicum.konkov.managers.TaskManager;

public class InMemoryTaskManagerTest extends TaskManagerTest {


    @Override
    public TaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }
}