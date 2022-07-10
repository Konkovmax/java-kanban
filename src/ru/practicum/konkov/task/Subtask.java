package ru.practicum.konkov.task;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;

import static ru.practicum.konkov.managers.InMemoryTaskManager.zone;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String[] lineContents) {
        super(lineContents);
        this.epicId = Integer.parseInt(lineContents[7]);
    }

    public Subtask(String name, String description, Status status, int epicId, String startTime, int duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public String toFileString() {
        return id + "," + "SUBTASK" + "," + name + ","
                + status.toString() + "," + description + "," + ((startTime == null) ? "" : startTime.format(DATE_TIME_FORMATTER)) + "," +
                duration + "," + epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), epicId);
        result = 31 * result + epicId;
        return result;
    }
}