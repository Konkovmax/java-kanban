package ru.practicum.konkov.API;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import ru.practicum.konkov.task.Status;
import ru.practicum.konkov.task.Subtask;
import ru.practicum.konkov.task.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static ru.practicum.konkov.managers.InMemoryTaskManager.zone;
import static ru.practicum.konkov.task.Task.DATE_TIME_FORMATTER;

public class SubtaskAdapter extends TypeAdapter<Subtask> {
    @Override
    public void write(JsonWriter writer, Subtask task) throws IOException {
        writer.beginObject();
        writer.name("id");
        writer.value(task.getId());
        writer.name("name");
        writer.value(task.getName());
        writer.name("description");
        writer.value(task.getDescription());
        writer.name("status");
        writer.value(task.getStatus().toString());
        writer.name("epicId");
        writer.value(task.getEpicId());
        if(task.getStartTime()!=null){
            writer.name("startTime");
            writer.value(task.getStartTime().format(DATE_TIME_FORMATTER));
            writer.name("duration");
            writer.value(task.getDuration());
        }
        writer.endObject();
    }

    @Override
    public Subtask read(JsonReader reader) throws IOException {
        Subtask task = new Subtask("name","descr",Status.NEW,2);
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
            } if ("epicId".equals(fieldname)) {
                token = reader.peek();
                task.setEpicId(reader.nextInt());
            }
            if("description".equals(fieldname)) {
                token = reader.peek();
                task.setDescription(reader.nextString());
            }
            if ("name".equals(fieldname)) {
                token = reader.peek();
                task.setName(reader.nextString());
            }
            if("status".equals(fieldname)) {
                token = reader.peek();
                task.setStatus(Status.valueOf(reader.nextString()));
            }
            if ("startTime".equals(fieldname)) {
                token = reader.peek();
                task.setStartTime(ZonedDateTime.of(LocalDateTime.parse(reader.nextString(), DATE_TIME_FORMATTER), zone));
            }
            if("duration".equals(fieldname)) {
                token = reader.peek();
                task.setDuration(reader.nextInt());
            }
        }
        reader.endObject();
        return task;
    }
}
