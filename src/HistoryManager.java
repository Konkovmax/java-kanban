import java.util.List;

public interface HistoryManager {

    void linkLast(Task task);

    void remove(int id);

    List<Task> getViewHistory();

    void getTasks();

}
