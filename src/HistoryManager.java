import java.util.List;
import ru.practicum.objects.Task;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getViewHistory();

    void getTasks();
}
