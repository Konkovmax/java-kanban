import java.util.HashMap;

public class Epic extends Task{
    HashMap<Integer, Subtask> subtaskList= new HashMap<>();

    public Epic(String name, String description, byte status) {
        super(name, description, status);
    }

    public Epic(String name, String description) {
        super(name, description, (byte)0);
    }
}
