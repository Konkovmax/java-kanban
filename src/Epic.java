import java.util.HashMap;

public class Epic extends Task{
    HashMap<Integer, Subtask> subtaskList= new HashMap<>();

    public Epic(String name, String description, byte status) {
        super(name, description, status);
    }

    public Epic(String name, String description) {
        super(name, description, (byte)0);
    }

    public void setEpicStatus() {
        byte statusSum = 0;
        int result;
        for (Subtask subtask : subtaskList.values()) {
            statusSum += subtask.status;
        }
        if (statusSum == 0) {
            result = 0;
        } else result = ((statusSum / subtaskList.size()) == 2) ? 2 : 1;
        this.status= (byte) result;
    }
}
