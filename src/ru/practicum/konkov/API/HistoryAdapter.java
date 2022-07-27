package ru.practicum.konkov.API;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.practicum.konkov.managers.InMemoryHistoryManager;
import ru.practicum.konkov.managers.Managers;
import ru.practicum.konkov.task.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends TypeAdapter<InMemoryHistoryManager> {

    @Override
    public void write(JsonWriter writer, InMemoryHistoryManager historyManager) throws IOException {
        List<Task> history = historyManager.getViewHistory();
        writer.beginArray();
        for (Task task : history){
            writer.value(task.toString());
        }
        writer.endArray();
    }

    @Override
    public InMemoryHistoryManager read(JsonReader jsonReader) throws IOException {
        InMemoryHistoryManager historyManager = (InMemoryHistoryManager) Managers.getDefault();
        List<String> history = new ArrayList<>();
        jsonReader.beginArray();
        while (jsonReader.hasNext()){
            history.add(jsonReader.nextString());
        }
        jsonReader.endArray();
        return historyManager;
    }
}
