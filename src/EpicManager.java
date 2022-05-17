import java.util.HashMap;

public interface EpicManager {

    void setStatus(Status status);

    HashMap<Integer, Subtask> getSubtasks();

    void setSubtasks(HashMap<Integer, Subtask> subtasks);

}
