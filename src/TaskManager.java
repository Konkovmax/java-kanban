import ru.practicum.objects.*;
import java.util.List;

public interface TaskManager {

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    void removeAll(); //point 2.2 technical specification of sprint 3

    Task getTaskById(int id);

    void printTasks();

    void printEpics();

    void printEpicSubtasks(Epic epic);

    void printSubtasks();

    void printViewHistory();

    Task getSubtaskById(int id);

    Task getEpicById(int id);

    List<Task> getHistory();

}
