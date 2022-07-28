package ru.practicum.konkov.tests;

import org.junit.jupiter.api.Test;
import ru.practicum.konkov.managers.Managers;
import ru.practicum.konkov.managers.TaskManager;
import ru.practicum.konkov.task.Epic;
import ru.practicum.konkov.task.Status;
import ru.practicum.konkov.task.Subtask;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    TaskManager manager = Managers.getDefault();
    Epic epic = new Epic("epic for tests", "comprehensive testing");

    EpicTest() throws IOException {
    }

    public void createSubtasks(Status subtask1Status, Status subtask2Status, Status subtask3Status) {
        manager.addEpic(epic);
        int epicId = epic.getId();
        Subtask subtask1 = new Subtask("subtask #1", "", subtask1Status, epicId);
        Subtask subtask2 = new Subtask("subtask #2", "", subtask2Status, epicId);
        Subtask subtask3 = new Subtask("subtask #3", "", subtask3Status, epicId);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

    }

    @Test
    public void shouldStatusBeNewWithoutSubtasks() {
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void shouldStatusBeNewWithSubtasksAllNew() {
        createSubtasks(Status.NEW, Status.NEW, Status.NEW);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void shouldStatusBeDoneWithSubtasksAllDone() {
        createSubtasks(Status.DONE, Status.DONE, Status.DONE);
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void shouldStatusBeInProgressWithSubtasksNewAndDone() {
        createSubtasks(Status.DONE, Status.NEW, Status.DONE);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldStatusBeInProgressWithSubtasksInProgress() {
        createSubtasks(Status.IN_PROGRESS, Status.IN_PROGRESS, Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }


}