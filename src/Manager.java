import java.util.HashMap;

public class Manager {

    HashMap<Integer, Task> taskList = new HashMap<>();
    HashMap<Integer, Epic> epicList = new HashMap<>();
    int idMaker = 0;

    public void addTask(Task task) {
        task.setId(idMaker);
        taskList.put(idMaker, task);
        idMaker++;
    }

    public void addEpic(Epic epic) {
        epic.setId(idMaker);
        epicList.put(idMaker, epic);
        idMaker++;
    }

    public void addSubtask(Epic epic, Subtask subtask) {
        subtask.setId(idMaker);
        epic.subtaskList.put(idMaker, subtask);
        idMaker++;
        epic.setEpicStatus();
    }

    public void updateTask(Task task) {
        taskList.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epicList.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        for (Epic epic : epicList.values()) {
            if (epic.subtaskList.containsKey(subtask.getId())){
                epic.subtaskList.put(subtask.getId(), subtask);
                epic.setEpicStatus();
            }
        }
    }

    public void deleteTask(int id) {
        taskList.remove(id);
    }

    public void deleteEpic(int id) {
        epicList.remove(id);
    }

    public void deleteSubtask(int id) {
        for (Epic epic : epicList.values()) {
            if (epic.subtaskList.containsKey(id)){
                epic.subtaskList.remove(id);
                epic.setEpicStatus();
            }
        }

    }

    public void removeAll(){//point 2.2 technical specification
        taskList.clear();
        epicList.clear();
    }

    public Task getTaskById(int id){
        Task targetTask=new Task("Task not found","",(byte)(-1));
        if(taskList.containsKey(id)){
            targetTask=taskList.get(id);
        }else if (epicList.containsKey(id)){
            targetTask=epicList.get(id);
        }else {
            for (Epic epic : epicList.values()) {
                if (epic.subtaskList.containsKey(id)) {
                    targetTask=epic.subtaskList.get(id);
                }
            }
        }
        return targetTask;
    }

    public void printTask() {
        for (Task task : taskList.values()) {
            System.out.println("Task: " + task);
        }
    }

    public void printEpic() {
        for (Epic epic : epicList.values()) {
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
