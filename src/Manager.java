import java.util.HashMap;

public class Manager {

    HashMap<Integer, Task> taskList= new HashMap<>();
    HashMap<Integer, Epic> epicList= new HashMap<>();
    int idMaker=0;

    public void addTask(Task task){
        taskList.put(idMaker, task);
        idMaker++;
    }

    public void addEpic(Epic epic){
        epicList.put(idMaker, epic);
        idMaker++;
    }

    public void addSubtask(Epic epic,Subtask subtask){
        epic.subtaskList.put(idMaker, subtask);
        idMaker++;
        epic.status=setEpicStatus(epic);
    }

    public void updateTask(Task task){
        taskList.put(task.getId, task);
        idMaker++;
    }

    public void addEpic(Epic epic){
        epicList.put(idMaker, epic);
        idMaker++;
    }

    public void addSubtask(Epic epic,Subtask subtask){
        epic.subtaskList.put(idMaker, subtask);
        idMaker++;
        epic.status=setEpicStatus(epic);
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

     public void printSubtask(Epic epic){
     for (Subtask subtask: epic.subtaskList.values()){
         System.out.println("Subtask: " + subtask);
     }
    }

    public byte setEpicStatus(Epic epic){
        byte statusSum = 0;
        int result;
        for (Subtask subtask: epic.subtaskList.values()){
            statusSum+=subtask.status;
        }
        if(statusSum == 0){
            result = 0;
        } else result = ((statusSum/epic.subtaskList.size()) == 2)?2:1;
        return (byte)result;
    }
}
