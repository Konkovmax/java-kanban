import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Epic extends Task {

    private Map<Integer, Subtask> subtasks = new HashMap<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
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

    public void setStatus(Status status) {
        this.status = status;
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(HashMap<Integer, Subtask> subtasks) {
        this.subtasks = subtasks;
    }
}
