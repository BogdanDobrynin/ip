public class Task {
    protected String description;
    private boolean isDone;

    public Task(String description, Boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

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

    @Override
    public String toString() {
        String status = isDone ? "[X]" : "[ ]";
        return status + " " + description;
    }
}
