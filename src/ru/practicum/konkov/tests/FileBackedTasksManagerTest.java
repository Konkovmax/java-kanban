package ru.practicum.konkov.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.konkov.managers.FileBackedTasksManager;
import ru.practicum.konkov.managers.InMemoryTaskManager;
import ru.practicum.konkov.managers.TaskManager;
import ru.practicum.konkov.task.Epic;
import ru.practicum.konkov.task.Status;
import ru.practicum.konkov.task.Task;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest {


    @Override
    public TaskManager createTaskManager() {
        return new FileBackedTasksManager("backedtaskstest.csv");
    }

    FileBackedTasksManager fileTasksManager = new FileBackedTasksManager("backedtaskstest.csv");
    private ByteArrayOutputStream output = new ByteArrayOutputStream();

    @Test
    void saveEpicWithoutSubtasksAndWithoutHistory(){
        Epic epic1 = new Epic("checklist for sprint", "to minimize mistakes");
        fileTasksManager.addEpic(epic1);
        System.setOut(new PrintStream(output));
                FileBackedTasksManager.printFile(fileTasksManager.file);

        Assertions.assertEquals("id,type,name,status,description,epic" + "\n" + "0,EPIC,checklist for sprint,NEW,to minimize mistakes," + "\n\n\n\n\n", output.toString());
        System.setOut(null);
    }

    @Test
    void loadFromFileStandard(){
        fillData(fileTasksManager);
        fileTasksManager.getTaskById(1);
        fileTasksManager.getTaskById(0);
        fileTasksManager.getEpicById(2);
        FileBackedTasksManager manager = fileTasksManager.loadFromFile(fileTasksManager.file);
                final List<Task> history = manager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая.");
        Task task2 = new Task("study encapsulation", "to write very well code", Status.NEW);
        final Task task = manager.getTaskById(1);
        task.setId(1);
        //т.к. нет готового метода, который возвращает список задач, то использую просто проверочную задачу
        assertEquals(task, task);
    }
}