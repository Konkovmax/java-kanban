import java.util.HashMap;

public class Manager {

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();
    int id = 0;

    public void addTask(Task task) {
        task.setId(id);
        tasks.put(id, task);
        generateNewId();
    }

    public void addEpic(Epic epic) {
        epic.setId(id);
        epics.put(id, epic);
        generateNewId();
    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(id);
        subtasks.put(id, subtask);
        generateNewId();
        epics.get(subtask.epicId).setStatus(setEpicStatus(subtask.epicId));
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.epicId).setStatus(setEpicStatus(subtask.epicId));
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        epics.remove(id);
    }

    public void deleteSubtask(int id) {
        int epicId = subtasks.get(id).epicId;
        subtasks.remove(id);
        epics.get(epicId).setStatus(setEpicStatus(epicId));

    }

    public void removeAll(){//point 2.2 technical specification
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    public Task getTaskById(int id){
        Task targetTask=null;
        if(tasks.containsKey(id)){
            targetTask= tasks.get(id);
        }else if (epics.containsKey(id)){
            targetTask= epics.get(id);
        }else if (subtasks.containsKey(id)) {
            targetTask=subtasks.get(id);
        }
        return targetTask;
    }

    public void printTask() {
        for (Task task : tasks.values()) {
            System.out.println("Task: " + task);
        }
    }

    public void printEpic() {
        for (Epic epic : epics.values()) {
            System.out.println("Epic: " + epic);
            System.out.println("  Subtasks list:");
            printSubtask(epic.id);
            System.out.println(" ");
        }
    }

    public void printSubtask(int epicId) {
        for (Subtask subtask : subtasks.values()) {
            if(subtask.epicId==epicId) {
                System.out.println("Subtask: " + subtask);
            }
        }
    }

    public Status setEpicStatus(int epicId) {
        int statusSum = 0;
        int subtaskAmount=0;
        Status result;
        for (Subtask subtask : subtasks.values()) {
            if(subtask.epicId==epicId) {
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

    public void generateNewId(){
        id++;
    }


}
