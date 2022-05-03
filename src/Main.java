public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        fillData(manager);
        System.out.println(" Tasks list:");
        manager.printTasks();
        System.out.println(" Epics list:");
        manager.printEpics();
        updateStatus(manager);
        System.out.println(" Updated tasks list:");
        manager.printTasks();
        System.out.println(" Updated epics list:");
        manager.printEpics();
        deleteData(manager);
        System.out.println(" Tasks list after removal:");
        manager.printTasks();
        System.out.println(" Epics list after removal:");
        manager.printEpics();
    }

    public static void fillData(Manager manager){
        Task task1 = new Task("study java", "to write quality code", Status.IN_PROGRESS);
        Task task2 = new Task("study encapsulation", "to write very well code", Status.NEW);
        manager.addTask(task1);
        manager.addTask(task2);
        Epic epic1 = new Epic("preparation to sprint", "for execution project");
        Epic epic2 = new Epic("checklist for sprint", "to minimize mistakes");
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        Subtask subtask1 = new Subtask("Initialisation in constructor","", Status.NEW, 2);
        Subtask subtask2 = new Subtask("Line between methods","",Status.DONE, 3);
        Subtask subtask3 = new Subtask("visibility of variables","",Status.NEW, 3);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
    }

    public static void updateStatus(Manager manager){
        Task task;
        //update tasks status
        task = manager.getTaskById(0);
        task.status=Status.DONE;
        manager.updateTask(task);
        task = manager.getTaskById(1);
        task.status=Status.IN_PROGRESS;
        manager.updateTask(task);
        // update subtasks status
        task = manager.getTaskById(4);
        task.status=Status.IN_PROGRESS;
        manager.updateSubtask((Subtask) task);
        task = manager.getTaskById(6);
        task.status=Status.DONE;
        manager.updateSubtask((Subtask) task);
        //update epics name
        task = manager.getTaskById(2);
        task.name="NEW preparation to sprint";
        manager.updateEpic((Epic)task);
    }

    public static void deleteData(Manager manager){
        manager.deleteTask(1);
        manager.deleteEpic(2);
        manager.deleteSubtask(6);
    }

}
