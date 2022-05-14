import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager{

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();
    HistoryManager history = Managers.getDefaultHistory();
    int id = 0;

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
        //я подумал, что id в списке подзадач эпика и id для отдельного списка подзадач
        // должны быть одинаковые, но возможно, потом это окажется не правильным решением
        epics.get(subtask.epicId).subtasks.put(id, subtask);
        epics.get(subtask.epicId).setStatus(calculateEpicStatus(subtask.epicId));
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
        epics.get(subtask.epicId).subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.epicId).setStatus(calculateEpicStatus(subtask.epicId));
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        epics.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        int epicId = subtasks.get(id).epicId;
        subtasks.remove(id);
        epics.get(epicId).subtasks.remove(id);
        epics.get(epicId).setStatus(calculateEpicStatus(epicId));

    }

    @Override
    public void removeAll() {//point 2.2 technical specification of sprint 3
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task targetTask = null;
        if (tasks.containsKey(id)) {
            targetTask = tasks.get(id);
        } else if (epics.containsKey(id)) {
            targetTask = epics.get(id);
        } else if (subtasks.containsKey(id)) {
            targetTask = subtasks.get(id);
        }

        history.updateViewHistory(targetTask);
        return targetTask;
    }


    @Override
    public void printTasks() {
        for (Task task : tasks.values()) {
            System.out.println("Task: " + task);
            history.updateViewHistory(task);
        }
    }

    @Override
    public void printEpics() {
        for (Epic epic : epics.values()) {
            System.out.println("Epic: " + epic);
            history.updateViewHistory(epic);
            System.out.println("  Subtasks list:");
            printEpicSubtasks(epic);
            System.out.println(" ");
        }
    }

    @Override
    public void printEpicSubtasks(Epic epic) {
        for (Subtask subtask : epic.subtasks.values()) {
            System.out.println("Subtask: " + subtask);
            history.updateViewHistory(subtask);
        }
    }

    @Override
    public void printSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            System.out.println("Subtask: " + subtask);
            history.updateViewHistory(subtask);
        }
    }

    @Override
    public Status calculateEpicStatus(int epicId) {
        int statusSum = 0;
        int subtaskAmount = 0;
        Status result;
        for (Subtask subtask : subtasks.values()) {
            if (subtask.epicId == epicId) {
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
    public void printViewHistory(){
        System.out.println(" Viewed tasks from latest to oldest");
        for(int i = (history.getHistory().size() - 1);i>=0; i--) {
            System.out.println(history.getHistory().get(i));
        }
    }



    private void generateNewId() {
        id++;
    }


}
