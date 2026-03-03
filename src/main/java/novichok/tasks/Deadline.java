package novichok.tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a task that needs to be done by a specific date and time.
 * Unlike a simple ToDo, this class uses LocalDateTime to actually understand
 * the date, which allows us to do things like filtering by date.
 */

public class Deadline extends Task {
    private LocalDateTime by;
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    /**
     * Creates a new Deadline task.
     *
     * @param description What the task is about.
     * @param date The date string, which must match the d/M/yyyy HHmm format.
     * @throws DateTimeParseException If the date string provided is formatted incorrectly.
     */

    public Deadline(String description, String date) {
        super(description);
        this.by = LocalDateTime.parse(date, INPUT_FORMAT);
    }

    /**
     * Checks if the deadline falls on a specific date.
     * This is particularly useful for the "filter" command.
     *
     * @param date The date string to check against.
     * @return true if the dates match (ignoring time), false otherwise.
     */

    public boolean isOn(String date) {
        try {
            LocalDateTime targetDateTime = LocalDateTime.parse(date, INPUT_FORMAT);
            return this.by.toLocalDate().equals(targetDateTime.toLocalDate());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Converts the deadline into a format that can be easily saved to a text file.
     *
     * @return A pipe-separated string representing the deadline.
     */

    @Override
    public String toFileFormat() {
        // Ensure the date is formatted back into the string format your constructor expects
        String dateString = by.format(java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy HHmm"));
        return "D | " + (getStatus() ? "1" : "0") + " | " + description + " | " + dateString;
    }

    /**
     * Returns a user-friendly string representation of the deadline.
     * The date is formatted into a more readable style (e.g., "Jan 01 2024").
     *
     * @return The formatted string for UI display.
     */

    @Override
    public String toString() {
        DateTimeFormatter out = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");
        return "[D]" + super.toString() + " (by: " + by.format(out) + ")";
    }
}
