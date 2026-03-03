package novichok.logic;

import novichok.exceptions.NovichokException;
import novichok.storage.Storage;
import novichok.tasks.Deadline;
import novichok.tasks.Event;
import novichok.ui.Ui;
import novichok.tasks.Task;
import novichok.tasks.ToDo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The TaskManager is essentially the "brain" of the application.
 * It handles the heavy lifting: managing the list of tasks, routing commands
 * to the right actions, and making sure everything gets saved to the hard drive.
 */


/**
 * Creates a new manager and tries to boot up the previous task list from disk.
 *
 * @param filePath Where the save file is located.
 * @param ui The user interface used to shout at (or inform) the user.
 */

public class TaskManager {
    private final List<Task> taskList = new ArrayList<>();
    private final Storage storage;
    private final Ui ui;

    // constructors
    public TaskManager(String filePath, Ui ui) {
        this.storage = new Storage(filePath);
        this.ui = ui;
        try {
            // You should implement this method in your Storage class
            this.taskList.addAll(storage.loadListFromDisk());
        } catch (IOException | NovichokException e) {
            ui.printCustomMessage("Warning: Could not load existing tasks. Starting fresh.");
        }
    }

    /**
     * Parses the user's input to create a Deadline.
     * It looks for the '/by' keyword to split the description from the date.
     *
     * @param args The raw string containing the description and deadline date.
     * @throws NovichokException If the user forgets the date or uses a weird format.
     */

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

    /**
     * Handles the creation of an Event.
     * It expects both a '/from' and a '/to' tag to define the timeframe.
     *
     * @param args The input string provided by the user.
     * @throws NovichokException If the user messes up the event syntax.
     */

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

    /**
     * Adds a simple ToDo task. Just the description
     *
     * @param args The description of the task.
     * @throws NovichokException If the user tries to add an empty ToDo.
     */

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

    /**
     * This is the main traffic controller. It takes the user's command word
     * and decides which method should handle the work. It also triggers
     * an auto-save if the task list was changed.
     *
     * @param commandAction The action to perform (e.g., "list", "todo", "delete").
     * @param args The rest of the user's input string.
     */

    public void executeCommand(String commandAction, String args) {
        try {
            boolean isListModified = true;

            switch (commandAction.toLowerCase()) {
                case "help":
                    ui.printMenu(5);
                    isListModified = false;
                    break;
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
                        ui.printCustomMessage("No tasks found matching that criteria.");
                    } else {
                        ui.printCustomMessage("Here are the matching tasks in your list:");
                        for (int i = 0; i < filtered.size(); i++) {
                            ui.printCustomMessage("\t" + (i + 1) + ". " + filtered.get(i));
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
            ui.printCustomMessage(e.getMessage());
        } catch (IOException e) {
            ui.printCustomMessage("Novichok: Could not save tasks to disk. " + e.getMessage());
        }
    }

    /**
     * Searches through the tasks based on specific criteria like date, name, or type.
     *
     * @param args The filter type and value (e.g., "/date 19/2/2026").
     * @return A list of tasks that survived the filtering process.
     * @throws NovichokException If the filter arguments are missing or invalid.
     */

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

    /**
     * Grabs the entire current task list.
     *
     * @return The list of tasks.
     */

    public List<Task> getTaskList() {
        return this.taskList;
    }

    /**
     * Helper method to tell the user that a task was successfully added.
     */

    private void printAddedMessage(Task task) {
        ui.printCustomMessage("The following task has been added to the list:");
        ui.printCustomMessage("\t" + task.toString());
        ui.printCustomMessage("Your list has " + taskList.size() + " task(s) in progress");
    }

    /**
     * Helper method to confirm to the user that a task was wiped from the list.
     */

    private void printDeletedMessage(Task task) {
        ui.printCustomMessage("Executed. The following task is no more:");
        ui.printCustomMessage("\t" + task.toString());
        ui.printCustomMessage("There are " + taskList.size() + " tasks remaining.");
    }

    /**
     * Loops through all tasks and prints them in a numbered list format.
     */

    public void printList() {
        int j = 1;
        for (Task task: getTaskList()) {
            ui.printCustomMessage("\t" + j + ". " + task.toString());
            j++;
        }
    }

    /**
     * Takes a string of task numbers, turns them into actual integers,
     * removes duplicates, and sorts them in reverse.
     *
     * Why reverse? Because if we delete index 1, index 2 becomes the new index 1.
     * Deleting from the back prevents this "shifting" headache.
     *
     * @param args String input from user (e.g., "3 1 2").
     * @return A list of clean, 0-indexed integers.
     * @throws NovichokException If the user types something that isn't a number.
     */

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

    /**
     * Sets a task's status to done or not done.
     */

    public void setTaskStatus(Task task, boolean isDone) {
        task.setStatus(isDone);
    }

    /**
     * Processes "mark" or "unmark" requests for one or multiple tasks.
     *
     * @param action Either "mark" or "unmark".
     * @param args The task number(s) the user wants to update.
     */

    public void taskStatusUpdate(String action, String args) {
        try {
            List<Integer> indices = sanitizeIndices(args);
            boolean statusToSet = action.equalsIgnoreCase("mark");
            List<String> errors = new ArrayList<>();

            for (int index : indices) {
                if (index >= 0 && index < taskList.size()) {
                    Task task = taskList.get(index);
                    setTaskStatus(task, statusToSet);
                    ui.printCustomMessage("Task " + (index + 1) + " updated: " + task);
                } else {
                    errors.add(String.valueOf(index + 1));
                }
            }

            if (!errors.isEmpty()) {
                ui.printCustomMessage("Notice: The following indices were out of bounds: " + String.join(", ", errors));
            }
        } catch (NovichokException e) {
            ui.printCustomMessage(e.getMessage());
        }
    }
}
