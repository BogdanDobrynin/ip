import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a manager that handles the list of task
 */
public class TaskManager {
    private final List<Task> taskList = new ArrayList<>();

    // constructors
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

    // prints all recorded tasks
    public void printList() {
        int j = 1;
        for (Task task: getTaskList()) {
            System.out.println("\t" + j + ". " + task.toString());
            j++;
        }
    }

    // updates tasks to done/undone
    public void updateTask(String update, Task task) {
        if (update.equalsIgnoreCase("unmark")) {
            task.setStatus(false);
        } else if (update.equalsIgnoreCase("mark")) {
            task.setStatus(true);
        }
    }

    // handles bulk task update
    public void taskStatusUpdate(String updateString) {
        String[] update = updateString.split(" ");
        for (int i = 1; i < update.length; i++) {
             try {
                 Task taskToUpdateIndex = getTaskList().get(Integer.parseInt(update[i]) - 1);
                 updateTask(update[0], taskToUpdateIndex);
                 System.out.println("Task " + update[i] + " has been updated");
                 System.out.println(taskToUpdateIndex.toString());

             } catch (Exception error) {
                 System.out.println("Task " + update[i] + " wasn't successfully updated");
             }
        }
    }
}
