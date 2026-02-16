package novichok.logic;

import novichok.exceptions.NovichokException;
import novichok.storage.Storage;
import novichok.tasks.Deadline;
import novichok.tasks.Event;
import novichok.tasks.Task;
import novichok.tasks.ToDo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a manager that handles the list of task
 */

public class TaskManager {
    private final List<Task> taskList = new ArrayList<>();
    private final Storage storage;

    // constructors
    public TaskManager(String filePath) {
        this.storage = new Storage(filePath);
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
        try {
            List<Integer> indices = sanitizeIndices(args);
            boolean statusToSet = action.equalsIgnoreCase("mark");
            List<String> errors = new ArrayList<>();

            for (int index : indices) {
                if (index >= 0 && index < taskList.size()) {
                    Task task = taskList.get(index);
                    setTaskStatus(task, statusToSet);
                    System.out.println("Task " + (index + 1) + " updated: " + task);
                } else {
                    errors.add(String.valueOf(index + 1));
                }
            }

            if (!errors.isEmpty()) {
                System.out.println("Notice: The following indices were out of bounds: " + String.join(", ", errors));
            }
        } catch (NovichokException e) {
            System.out.println(e.getMessage());
        }
    }

    //** User Action passes the action to take, user command parses the arguments
    public void executeCommand(String commandAction, String args) {
        try {
            boolean isListModified = true;

            switch (commandAction.toLowerCase()) {
            case "list":
                printList();
                isListModified = false;break;
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
            case "delete":
                deleteTask(args);
                break;
            default:
                throw new NovichokException("This is not a valid command");
            }

            if (isListModified) {
                storage.saveListToDisk(taskList);
            }

        } catch (NovichokException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Novichok: Could not save tasks to disk. " + e.getMessage());        }
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

    private void addToDo(String args) throws NovichokException {
        if (args.trim().isEmpty()) {
            throw new NovichokException("The 'to-do' has no description, skipping...");
        }
        ToDo toDoTask = new ToDo(args);
        taskList.add(toDoTask);
        printAddedMessage(toDoTask);
    }

    private void deleteTask(String args) throws NovichokException {
        List<Integer> indicesToDelete = sanitizeIndices(args);
        List<String> outOfBounds = new ArrayList<>();

        // processes indices to delete
        for (int index : indicesToDelete) {
            if (index >= 0 && index < taskList.size()) {
                Task taskToRemove = this.taskList.remove(index);
                printDeletedMessage(taskToRemove);
            } else {
                outOfBounds.add(String.valueOf(index + 1));
            }
        }

        // Handles out of bound indices safely
        if (!outOfBounds.isEmpty()) {
            throw new NovichokException("The following indices were out of bounds: " +
                    String.join(", ", outOfBounds));
        }
    }

    private void printAddedMessage(Task task) {
        System.out.println("The following task has been added to the list:");
        System.out.println("\t" + task.toString());
        System.out.println("Your list has " + taskList.size() + " task(s) in progress");
    }

    private void printDeletedMessage(Task task) {
        System.out.println("Executed. The following task is no more:");
        System.out.println("\t" + task.toString());
        System.out.println("There are " + taskList.size() + " tasks remaining.");
    }

    // User input sanitization
    private List<Integer> sanitizeIndices(String args) throws NovichokException {
        String[] inputs = args.trim().split("\\s+");
        List<Integer> indices = new ArrayList<>();

        for (String input : inputs) {
            try {
                int index = Integer.parseInt(input);
                indices.add(index);
            } catch (NumberFormatException e) {
                throw new NovichokException("'" + input + "' is not a valid task number. " +
                        "Please use numbers (e.g., '1 3').");
            }
        }

        // Return a cleaned, distinct, and reverse-sorted list
        return indices.stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .map(i -> i - 1) // Now index 1 becomes 0, 5 becomes 4
                .toList();
    }
}
