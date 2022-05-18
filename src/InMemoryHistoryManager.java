import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> viewHistory = new ArrayList<>();
    private int maxRecordsQuantity;

    public InMemoryHistoryManager(int maxRecordsQuantity) {
        this.maxRecordsQuantity = maxRecordsQuantity;
    }

    @Override
    public List<Task> getViewHistory() {
        return viewHistory;
    }

    @Override
    public void add(Task task) {
        if (viewHistory.size() == maxRecordsQuantity) {
            viewHistory.remove(0);
        }
        viewHistory.add(task);
    }
}
