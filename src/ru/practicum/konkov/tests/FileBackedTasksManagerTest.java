package ru.practicum.konkov.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.konkov.exceptions.EmptyListException;
import ru.practicum.konkov.exceptions.ManagerSaveException;
import ru.practicum.konkov.managers.FileBackedTasksManager;
import ru.practicum.konkov.managers.TaskManager;
import ru.practicum.konkov.task.Epic;
import ru.practicum.konkov.task.Status;
import ru.practicum.konkov.task.Subtask;
import ru.practicum.konkov.task.Task;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
    void saveEpicWithoutSubtasksAndWithoutHistory() {
        Epic epic1 = new Epic("checklist for sprint", "to minimize mistakes");
        fileTasksManager.addEpic(epic1);
        String fileContents = null;
        // System.setOut(new PrintStream(output));
        //  FileBackedTasksManager.printFile(fileTasksManager.file);
        try {
            fileContents = Files.readString(fileTasksManager.file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String expected = "id,type,name,status,description,startTime,duration,epic" +
                "\n" + "0,EPIC,checklist for sprint,NEW,to minimize mistakes," + "\n\n";
        Assertions.assertEquals(expected, fileContents);
        System.setOut(null);
    }

    @Test
    void loadFromFileStandard() {
        fillData(fileTasksManager);
        fileTasksManager.getTaskById(1);
        fileTasksManager.getTaskById(0);
        fileTasksManager.getEpicById(2);
        FileBackedTasksManager manager = fileTasksManager.loadFromFile(fileTasksManager.file);
        final List<Task> history = manager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая.");
        //т.к. нет готового метода, который возвращает список задач, то использую просто проверочную задачу
        Task expectedTask = new Task("2study encapsulation", "to write very well code", Status.NEW,"01.07.22 11:15",60);
        final Task task = manager.getTaskById(1);
        expectedTask.setId(1);
        assertEquals(expectedTask, task);

    }

    @Test
    void loadFromFileWithoutTasks() {
        String header = "id,type,name,status,description,startTime,duration,epic";
        try (FileWriter fileWriter = new FileWriter(fileTasksManager.file, StandardCharsets.UTF_8)) {
            fileWriter.write(header + "\n");

        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        FileBackedTasksManager manager = fileTasksManager.loadFromFile(fileTasksManager.file);
        final List<Task> history = manager.getHistory();
        assertTrue(history.isEmpty());
        EmptyListException ex = assertThrows(
                EmptyListException.class,
                () -> {
                    manager.getTaskById(0);
                });
        Assertions.assertEquals("List is empty", ex.getMessage());
    }

    @Test
    void loadFromFileWithoutHistory() {
        fillData(fileTasksManager);
        FileBackedTasksManager manager = fileTasksManager.loadFromFile(fileTasksManager.file);
        final List<Task> history = manager.getHistory();
        assertTrue(history.isEmpty());
        }

}