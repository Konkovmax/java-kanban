import java.util.HashMap;

public class Epic extends Task{
    //HashMap<Integer, Subtask> subtaskList= new HashMap<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public void setStatus(Status status){
        this.status=status;
    }
}
