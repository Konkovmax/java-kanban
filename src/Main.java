public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        fillData(manager);
        System.out.println("Tasks list:");
        manager.printTask();
        System.out.println("Epics list:");
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
        manager.addSubtask(epic1,subtask3);



    }

}
