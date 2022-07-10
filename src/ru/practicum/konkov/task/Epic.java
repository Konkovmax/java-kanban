package ru.practicum.konkov.task;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

import static ru.practicum.konkov.managers.InMemoryTaskManager.zone;

public class Epic extends Task {

    protected ZonedDateTime endTime;
    private List<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(String name, String description, String startTime, int duration) {
        super(name, description, Status.NEW, startTime, duration);
        this.endTime = ZonedDateTime.of(LocalDateTime.parse(startTime, DATE_TIME_FORMATTER), zone).plusMinutes(duration);

    }

    public Epic(String name, String description, Status status, String startTime, int duration) {
        super(name, description, status, startTime, duration);
        this.endTime = ZonedDateTime.of(LocalDateTime.parse(startTime, DATE_TIME_FORMATTER), zone).plusMinutes(duration);
    }

    public Epic(String[] lineContents) {
        this.name = lineContents[2];
        this.description = lineContents[4];
        this.status = Status.valueOf(lineContents[3]);
        this.id = Integer.parseInt(lineContents[0]);
    }

    @Override
    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void calculateDuration() {
        duration = 0;
        for (Subtask subtask : subtasks) {
            if ((endTime == null || endTime.isBefore(subtask.getEndTime())) &&
                    subtask.getStartTime() != null) {
                endTime = subtask.getEndTime();
            }
            if (startTime == null || subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
            duration += subtask.getDuration();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), subtasks);
        result = 31 * result + subtasks.hashCode();
        return result;
    }

    public String toFileString() {
        return id + "," + "EPIC" + "," + name + ","
                + status.toString() + "," + description + ",";
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }
}
