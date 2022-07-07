package ru.practicum.konkov.task;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
// решил, что логично если зоной будет управлять Таск менеджер
import static ru.practicum.konkov.managers.InMemoryTaskManager.zone;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected Status status;
    protected int duration;
    protected ZonedDateTime startTime;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, Status status, String startTime, int duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = ZonedDateTime.of(LocalDateTime.parse(startTime, DATE_TIME_FORMATTER), zone);
        this.duration = duration;
    }

    public Task(String[] lineContents) {
        this.name = lineContents[2];
        this.description = lineContents[4];
        this.status = Status.valueOf(lineContents[3]);
        this.id = Integer.parseInt(lineContents[0]);

    }

    public ZonedDateTime getEndTime(){
        return startTime.plusMinutes(duration);
    }

    @Override
    public String toString() {
        return " {name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +'\'' +
                ", startTime=" + ((startTime== null) ? "" : startTime.format(DATE_TIME_FORMATTER)) +'\'' +
                ", duration=" + duration +

                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && name.equals(task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, description, status);
        result = 31 * result + name.hashCode();
        return result;
    }

    public String toFileString() {
        return id + "," + "TASK" + "," + name + ","
                + status.toString() + "," + description + ",";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getDuration() {
        return duration;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }
}
