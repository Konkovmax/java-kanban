import java.util.HashMap;

public class Manager {

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    int id = 0;

    public void addTask(Task task) {
        task.setId(id);
        tasks.put(id, task);
        id++;
    }

    public void addEpic(Epic epic) {
        epic.setId(id);
        epics.put(id, epic);
        id++;
    }

    public void addSubtask(Epic epic, Subtask subtask) {
        subtask.setId(id);
        epic.subtaskList.put(id, subtask);
        id++;
        epic.setEpicStatus();
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        for (Epic epic : epics.values()) {
            if (epic.subtaskList.containsKey(subtask.getId())){
                epic.subtaskList.put(subtask.getId(), subtask);
                epic.setEpicStatus();
            }
        }
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        epics.remove(id);
    }

    public void deleteSubtask(int id) {
        for (Epic epic : epics.values()) {
            if (epic.subtaskList.containsKey(id)){
                epic.subtaskList.remove(id);
                epic.setEpicStatus();
            }
        }

    }

    public void removeAll(){//point 2.2 technical specification
        tasks.clear();
        epics.clear();
    }

    public Task getTaskById(int id){
        Task targetTask=null;
        if(tasks.containsKey(id)){
            targetTask= tasks.get(id);
        }else if (epics.containsKey(id)){
            targetTask= epics.get(id);
        }else {
            for (Epic epic : epics.values()) {
                if (epic.subtaskList.containsKey(id)) {
                    targetTask=epic.subtaskList.get(id);
                }
            }
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
            printSubtask(epic);
            System.out.println(" ");
        }
    }

    public void printSubtask(Epic epic) {// point 3.1 technical specification
        for (Subtask subtask : epic.subtaskList.values()) {
            System.out.println("Subtask: " + subtask);
        }
    }

    }
