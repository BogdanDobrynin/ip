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
                isListModified = false;
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
            case "delete":
                deleteTask(args);
                break;
                case "filter":
                    List<Task> filtered = filterBy(args);
                    if (filtered.isEmpty()) {
                        System.out.println("No tasks found matching that criteria.");
                    } else {
                        System.out.println("Here are the matching tasks in your list:");
                        for (int i = 0; i < filtered.size(); i++) {
                            System.out.println("\t" + (i + 1) + ". " + filtered.get(i));
                        }
                    }
                    isListModified = false;
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
        String[] parts = args.split(" /by ", 2);
        if (parts.length < 2) {
            throw new NovichokException("Error: Use 'deadline [desc] /by [d/M/yyyy HHmm]''");
        }

        if (parts[0].trim().isEmpty()) {
            throw new NovichokException("The 'deadline' has no description");
        }

        try {
            Deadline deadlineTask = new Deadline(parts[0].trim(), parts[1].trim());
            taskList.add(deadlineTask);
            printAddedMessage(deadlineTask);
        } catch (java.time.format.DateTimeParseException e) {
            throw new NovichokException("Format error! Please use: d/M/yyyy HHmm (e.g., 19/2/2026 1800)");
        }
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
        try {
            Event eventTask = new Event(parts[0], timeParts[0], timeParts[1]);
            taskList.add(eventTask);
            printAddedMessage(eventTask);
        } catch (java.time.format.DateTimeParseException e) {
            throw new NovichokException("Format error! Please use: d/M/yyyy HHmm (e.g., 19/2/2026 1800)");
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


    // TODO: increase robustness of filtering
    private List<Task> filterBy(String args) throws NovichokException {
        if (args.trim().isEmpty()) {
            throw new NovichokException("Filter command needs arguments (e.g., filter /date 12/12/2026 1800)");
        }

        String[] parts = args.split("\\s+", 2);
        if (parts.length < 2) {
            throw new NovichokException("Usage: filter [type] [value] (e.g., filter /name homework)");
        }

        String filterAction = parts[0].toLowerCase();
        String filterValue = parts[1].trim();
        List<Task> filteredTaskList = new ArrayList<>();

        switch (filterAction) {
        case "/date":
            try {
                for (Task task : taskList) {
                    if (task instanceof Deadline && ((Deadline) task).isOn(filterValue)) {
                        filteredTaskList.add(task);
                    } else if (task instanceof Event && ((Event) task).isOn(filterValue)) {
                        filteredTaskList.add(task);
                    }
                }
            } catch (java.time.format.DateTimeParseException e) {
                throw new NovichokException("To filter by date, please use the format: d/M/yyyy HHmm");
            }
            break;
        case "/name":
            for (Task task : taskList) {
                // Check if the description contains the keyword
                if (task.getDescription().toLowerCase().contains(filterValue.toLowerCase())) {
                    filteredTaskList.add(task);
                }
            }
            break;
        case "/type":
            for (Task task : taskList) {
                if (filterValue.equalsIgnoreCase("todo") && task instanceof ToDo) {
                    filteredTaskList.add(task);
                } else if (filterValue.equalsIgnoreCase("deadline") && task instanceof Deadline) {
                    filteredTaskList.add(task);
                } else if (filterValue.equalsIgnoreCase("event") && task instanceof Event) {
                    filteredTaskList.add(task);
                }
            }
            break;
            default:
                throw new NovichokException("Invalid filter type! Use /date, /name, or /type.");
        }
        return filteredTaskList;
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
