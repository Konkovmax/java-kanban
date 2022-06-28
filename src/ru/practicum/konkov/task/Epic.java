package ru.practicum.konkov.task;

import java.util.*;

public class Epic extends Task {

    private List<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(String[] lineContents) {
        super(lineContents);
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
