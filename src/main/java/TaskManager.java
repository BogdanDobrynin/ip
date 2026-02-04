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

    // array type task handling
    public void addTask(Task[] taskList) {
        this.taskList.addAll(Arrays.asList(taskList));
    }

    // list type task handling
    public void addTask(List<Task> taskList) {
        this.taskList.addAll(taskList);
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
    public void setTaskStatus(Task task, boolean isDone) {
        task.setStatus(isDone);
    }

    // Your bulk handler calls the specialist
    public void taskStatusUpdate(String action, String args) {
        String[] indices = args.split(" ");
        boolean statusToSet = action.equalsIgnoreCase("mark");

        for (String indexStr : indices) {
            try {
                int idx = Integer.parseInt(indexStr) - 1;
                Task task = taskList.get(idx);

                setTaskStatus(task, statusToSet);

                System.out.println("Task " + (idx + 1) + " updated: " + task);
            } catch (Exception e) {
                System.out.println("Could not update task: " + indexStr);
            }
        }
    }

    //** User Action passes the action to take, user command parses the arguments
    public void executeCommand(String commandAction, String args) {
        switch (commandAction.toLowerCase()) {
            case "list":
                printList();
                break;
            case "mark", "unmark":
                taskStatusUpdate(commandAction, args);
                break;
            case "todo":
                addToDo(args);
                break;
            case "deadline":
                addDeadline(args);
                break;
            case "event":
                addEvent(args);
                break;
            default:
                System.out.println("This is not a valid command");
        }
    }

    private void addToDo(String args) {
        if (args.trim().isEmpty()) {
            System.out.println("The 'to-do' has no description, skipping...");
            return;
        }
        Task newTask = new ToDo(args, false);
        taskList.add(newTask);
        printAddedMessage(newTask);
    }

    private void addDeadline(String args) {
        // format: [description] /by [time]
        String[] parts = args.split(" /by ", 2);
        if (parts.length < 2) {
            System.out.println("Error: Use 'deadline [desc] /by [time]'");
            return;
        }
        if (parts[0].trim().isEmpty()) {
            System.out.println("The 'deadline' has no description, skipping...");
            return;
        }
        Task newTask = new Deadline(parts[0], parts[1], false);
        taskList.add(newTask);
        printAddedMessage(newTask);
    }

    private void addEvent(String args) {
        if (args.trim().isEmpty()) {
            System.out.println("The event has no description, skipping...");
            return;
        }

        // Expected format: description /from time /to time
        String[] parts = args.split(" /from ", 2);
        if (parts.length < 2) {
            System.out.println("Error: Use 'event [desc] /from [start] /to [end]'");
            return;
        }
        String[] timeParts = parts[1].split(" /to ", 2);
        if (timeParts.length < 2) {
            System.out.println("Error: Missing '/to' section");
            return;
        }
        Task newTask = new Event(parts[0], timeParts[0], timeParts[1], false);
        taskList.add(newTask);
        printAddedMessage(newTask);
    }

    private void printAddedMessage(Task task) {
        System.out.println("The following task has been added to the list:");
        System.out.println("\t" + task.toString());
        System.out.println("Your list has " + taskList.size() + " task(s) in progress");
    }
}
