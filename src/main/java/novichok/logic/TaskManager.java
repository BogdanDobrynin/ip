package novichok.logic;

import novichok.tasks.Deadline;
import novichok.tasks.Event;
import novichok.exceptions.NovichokException;
import novichok.tasks.Task;
import novichok.tasks.ToDo;
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
        try {
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
                    throw new NovichokException("This is not a valid command");
            }
        } catch (NovichokException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addToDo(String args) throws NovichokException {
        if (args.trim().isEmpty()) {
            throw new NovichokException("The 'to-do' has no description, skipping...");
        }
        ToDo toDoTask = new ToDo(args);
        taskList.add(toDoTask);
        printAddedMessage(toDoTask);
    }

    private void addDeadline(String args) throws NovichokException {
        // format: [description] /by [time]
        String[] parts = args.split(" /by ", 2);
        if (parts.length < 2) {
            throw new NovichokException("Error: Use 'deadline [desc] /by [time]'");
        }
        if (parts[0].trim().isEmpty()) {
            throw new NovichokException("The 'deadline' has no description, skipping...");
        }
        Deadline deadlineTask = new Deadline(parts[0], parts[1]);
        taskList.add(deadlineTask);
        printAddedMessage(deadlineTask);
    }

    private void addEvent(String args) throws NovichokException {
        if (args.trim().isEmpty()) {
            throw new NovichokException("The event has no description, skipping...");
        }
        // Expected format: description /from time /to time
        String[] parts = args.split(" /from ", 2);
        if (parts.length < 2) {
            throw new NovichokException("Error: Use 'event [desc] /from [start] /to [end]'");
        }
        String[] timeParts = parts[1].split(" /to ", 2);
        if (timeParts.length < 2) {
            throw new NovichokException("Error: Missing '/to' section");
        }
        Event eventTask = new Event(parts[0], timeParts[0], timeParts[1]);
        taskList.add(eventTask);
        printAddedMessage(eventTask);
    }

    private void printAddedMessage(Task task) {
        System.out.println("The following task has been added to the list:");
        System.out.println("\t" + task.toString());
        System.out.println("Your list has " + taskList.size() + " task(s) in progress");
    }
}
