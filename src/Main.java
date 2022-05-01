public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        fillData(manager);
        System.out.println("Tasks list:");
        manager.printTask();
        System.out.println("Epics list:");
        manager.printEpic();
        updateStatus(manager);
        System.out.println("Updated tasks list:");
        manager.printTask();
        System.out.println(" Updated epics list:");
        manager.printEpic();



    }

    public static void fillData(Manager manager){
        Task task1 = new Task("study java", "to write quality code", (byte)1);
        Task task2 = new Task("study encapsulation", "to write very well code", (byte)0);
        manager.addTask(task1);
        manager.addTask(task2);
        Epic epic1 = new Epic("preparation to sprint", "for execution project");
        Epic epic2 = new Epic("checklist for sprint", "to minimize mistakes");
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        Subtask subtask1 = new Subtask("Initialisation in constructor","",(byte)0);
        Subtask subtask2 = new Subtask("Line between methods","",(byte)2);
        Subtask subtask3 = new Subtask("visibility of variables","",(byte)0);
        manager.addSubtask(epic1,subtask1);
        manager.addSubtask(epic2,subtask2);
        manager.addSubtask(epic2,subtask3);
    }

    public static void updateStatus(Manager manager){
        //update tasks status
        manager.taskList.get(0).status=2;
        manager.updateTask(manager.taskList.get(0));
        manager.taskList.get(1).status=1;
        // update subtasks status
        manager.updateTask(manager.taskList.get(1));
        manager.epicList.get(2).subtaskList.get(4).status=1;
        manager.updateSubtask(manager.epicList.get(2), manager.epicList.get(2).subtaskList.get(4));
        manager.epicList.get(3).subtaskList.get(6).status=2;
        manager.updateSubtask(manager.epicList.get(3), manager.epicList.get(3).subtaskList.get(6));
        //update epics name
        manager.epicList.get(2).name="NEW preparation to sprint";
        manager.updateEpic(manager.epicList.get(2));
    }

    public void deleteData(Manager manager){
        manager.deleteTask(manager.taskList.get(1));
        manager.
    }

}
