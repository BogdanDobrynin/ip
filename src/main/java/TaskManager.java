import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskManager {
    private final List<Task> taskList = new ArrayList<>();

    public TaskManager() {

    }

    public TaskManager(Task[] taskList) {
        addTask(taskList);
    }

    public TaskManager(List<Task> taskList) {
        addTask(taskList);
    }

    // array type task handling
    public void addTask(Task[] taskList) {
        this.taskList.addAll(Arrays.asList(taskList));
    }

    // list type task handling
    public void addTask(List<Task> taskList) {
        this.taskList.addAll(taskList);
    }

    // string type task handling
    public void addTask(String taskList) {
        this.taskList.add(new Task(taskList));
    }

    public List<Task> getTaskList() {
        return this.taskList;
    }

    public void printList() {
        int j = 1;
        for (Task task: getTaskList()) {
            System.out.println("\t" + j + ". " + task.toString());
            j++;
        }
    }
}
