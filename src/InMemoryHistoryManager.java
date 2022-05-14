import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    List<Task> viewHistory = new ArrayList<>();


    @Override
    public List<Task> getHistory(){
        return viewHistory;
    }

    @Override
    public void updateViewHistory(Task task){
        if (viewHistory.size()==10){
            viewHistory.remove(0);
        }
        viewHistory.add(task);
    }
}
