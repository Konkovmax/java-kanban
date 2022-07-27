package ru.practicum.konkov.API;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import ru.practicum.konkov.task.Status;
import ru.practicum.konkov.task.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static ru.practicum.konkov.managers.InMemoryTaskManager.zone;
import static ru.practicum.konkov.task.Task.DATE_TIME_FORMATTER;

public class TaskAdapter extends TypeAdapter<Task> {
    @Override
    public void write(JsonWriter writer, Task task) throws IOException {
        writer.beginObject();
        writer.name("id");
        writer.value(task.getId());
        writer.name("name");
        writer.value(task.getName());
        writer.name("description");
        writer.value(task.getDescription());
        writer.name("status");
        writer.value(task.getStatus().toString());
        if (task.getStartTime() != null) {
            writer.name("startTime");
            writer.value(task.getStartTime().format(DATE_TIME_FORMATTER));
            writer.name("description");
            writer.value(task.getDescription());
        }
        writer.endObject();
    }

    @Override
    public Task read(JsonReader reader) throws IOException {
        Task task = new Task();
        reader.beginObject();
        String fieldname = null;

        while (reader.hasNext()) {
            JsonToken token = reader.peek();

            if (token.equals(JsonToken.NAME)) {
                fieldname = reader.nextName();
            }
            if ("id".equals(fieldname)) {
                token = reader.peek();
                task.setId(reader.nextInt());
            }
            if ("description".equals(fieldname)) {
                token = reader.peek();
                task.setDescription(reader.nextString());
            }
            if ("name".equals(fieldname)) {
                token = reader.peek();
                task.setName(reader.nextString());
            }
            if ("status".equals(fieldname)) {
                token = reader.peek();
                task.setStatus(Status.valueOf(reader.nextString()));
            }
            if ("startTime".equals(fieldname)) {
                token = reader.peek();
                task.setStartTime(ZonedDateTime.of(LocalDateTime.parse(reader.nextString(), DATE_TIME_FORMATTER), zone));
            }
            if ("duration".equals(fieldname)) {
                token = reader.peek();
                task.setDuration(reader.nextInt());
            }
        }
        reader.endObject();
        return task;
    }
}
