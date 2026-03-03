package novichok.tasks;

public abstract class Task {
    protected String description;
    private boolean isDone;

    /**
     * This is the abstract base of all tasks in the app.
     * It holds the core data that every task needs: a description and a status
     * to track whether it's finished or not.
     *
     * use one of its children like ToDo, Deadline, or Event.
     */

    public Task(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Creates a brand new task.
     * By default, new tasks are marked as not done (because why add a finished task?).
     *
     * @param description What needs to be done.
     */

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public boolean getStatus() {
        return this.isDone;
    }

    public String getDescription() {
        return this.description;
    }

    public void setStatus(boolean status) {
        this.isDone = status;
    }

    public String toFileFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    @Override
    public String toString() {
        String status = isDone ? "[X]" : "[ ]";
        return status + " " + description;
    }
}
