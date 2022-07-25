package ru.practicum.konkov.tests;

import ru.practicum.konkov.managers.FileBackedTasksManager;
import ru.practicum.konkov.managers.TaskManager;

public class BackedFileTaskManagerTest extends TaskManagerTest {


    @Override
    public TaskManager createTaskManager() {
        return new FileBackedTasksManager("backedtasks.csv");
    }
}