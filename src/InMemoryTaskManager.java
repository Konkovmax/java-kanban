import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private HistoryManager history = Managers.getDefaultHistory();
    private int id = 0;

    @Override
    public void addTask(Task task) {
        task.setId(id);
        tasks.put(id, task);
        generateNewId();
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(id);
        epics.put(id, epic);
        generateNewId();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setId(id);
        subtasks.put(id, subtask);
        int epicId = subtask.getEpicId();
        epics.get(epicId).getSubtasks().put(id, subtask);
        epics.get(epicId).setStatus(calculateEpicStatus(epicId));
        generateNewId();
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).getSubtasks().put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).setStatus(calculateEpicStatus(subtask.getEpicId()));
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        for (Subtask subtask : epics.get(id).getSubtasks().values()) {
            history.remove(subtask.getId());
        }
        epics.get(id).getSubtasks().clear();
        epics.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        int epicId = subtasks.get(id).getEpicId();
        subtasks.remove(id);
        epics.get(epicId).getSubtasks().remove(id);
        epics.get(epicId).setStatus(calculateEpicStatus(epicId));
        history.remove(id);

    }

    @Override
    public void removeAll() {//point 2.2 technical specification of sprint 3
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Task getSubtaskById(int id) {
        Task targetTask = null;
        if (subtasks.containsKey(id)) {
            targetTask = subtasks.get(id);
        }

        history.linkLast(targetTask);
        return targetTask;
    }

    @Override
    public Task getTaskById(int id) {
        Task targetTask = null;
        if (tasks.containsKey(id)) {
            targetTask = tasks.get(id);
        }
        history.linkLast(targetTask);
        return targetTask;
    }

    @Override
    public Task getEpicById(int id) {
        Task targetTask = null;
        if (epics.containsKey(id)) {
            targetTask = epics.get(id);
        }
        history.linkLast(targetTask);
        return targetTask;
    }


    @Override
    public void printTasks() {
        for (Task task : tasks.values()) {
            System.out.println("Task: " + task);
            history.linkLast(task);
        }
    }

    @Override
    public void printEpics() {
        for (Epic epic : epics.values()) {
            System.out.println("Epic: " + epic);
            history.linkLast(epic);
            System.out.println("  Subtasks list:");
            printEpicSubtasks(epic);
            System.out.println(" ");
        }
    }

    @Override
    public void printEpicSubtasks(Epic epic) {
        for (Subtask subtask : epic.getSubtasks().values()) {
            System.out.println("Subtask: " + subtask);
            history.linkLast(subtask);
        }
    }

    @Override
    public void printSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            System.out.println("Subtask: " + subtask);
            history.linkLast(subtask);
        }
    }

    private Status calculateEpicStatus(int epicId) {
        int statusSum = 0;
        int subtaskAmount = 0;
        Status result;
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                subtaskAmount++;
                if (subtask.status == Status.DONE) {
                    statusSum += 2;
                }
                if (subtask.status == Status.IN_PROGRESS) {
                    statusSum += 1;
                }
            }
        }
        if (statusSum == 0) {
            result = Status.NEW;
        } else {
            result = (statusSum / subtaskAmount == 2) ? Status.DONE : Status.IN_PROGRESS;
        }
        return result;
    }

    @Override
    public void printViewHistory() {
        history.getTasks();
        System.out.println(" Viewed tasks from latest to oldest");
        for (int i = 0; i < history.getViewHistory().size(); i++) {
            System.out.println(history.getViewHistory().get(i));
        }
    }

    private void generateNewId() {
        id++;
    }

    @Override
    public List<Task> getHistory() {
        return history.getViewHistory();
    }


}
