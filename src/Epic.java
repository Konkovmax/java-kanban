import java.util.HashMap;

public class Epic extends Task{
    HashMap<Integer, Subtask> subtaskList= new HashMap<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public void setEpicStatus() {
        byte statusSum = 0;
        Status result;
        for (Subtask subtask : subtaskList.values()) {
            if(subtask.status==Status.DONE){
                statusSum +=2;
            }
            if(subtask.status==Status.IN_PROGRESS){
                statusSum+=1;
            }
        }
        if (statusSum == 0) {
            result = Status.NEW;
        } else {
            result = ((statusSum / subtaskList.size()) == 2) ? Status.DONE : Status.IN_PROGRESS;
        }
        this.status = result;
    }
}
