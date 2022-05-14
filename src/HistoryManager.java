import java.util.List;

public interface HistoryManager {

    void updateViewHistory(Task task);

    List<Task> getHistory();

}
