package novichok.tasks;

import novichok.exceptions.NovichokException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a task that occurs over a specific time range.
 * Unlike a simple Deadline, an Event needs both a start and an end point.
 * It also includes some basic logic to make sure the user doesn't try to
 * end an event before it actually begins.
 */


public class Event extends Task {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    // Use a static formatter to avoid creating a new one for every object
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

    /**
     * Creates a new Event.
     * We parse the date strings here and perform a quick sanity check on the timeline.
     *
     * @param description A brief summary of what the event is.
     * @param startDate The start date/time string.
     * @param endDate The end date/time string.
     * @throws NovichokException If the user tries to end the event before it starts.
     */

    public Event(String description, String startDate, String endDate) throws NovichokException {
        super(description);
        this.startDate = LocalDateTime.parse(startDate, INPUT_FORMAT);
        this.endDate = LocalDateTime.parse(endDate, INPUT_FORMAT);
        if (this.startDate.isAfter(this.endDate)) {
            throw new NovichokException("Error: Start time cannot be after end time. Time travel not supported!");
        }
    }

    /**
     * Checks if this event falls on a specific date.
     * This method is a helper for the filtering system.
     *
     * @param date The date string to look for.
     * @return true if either the start date or the end date matches the search date.
     */

    public boolean isOn(String date) {
        try {
            LocalDateTime targetDateTime = LocalDateTime.parse(date, INPUT_FORMAT);
            LocalDate searchDate = targetDateTime.toLocalDate();
            return searchDate.equals(this.startDate.toLocalDate()) ||
                    searchDate.equals(this.endDate.toLocalDate());
        } catch (DateTimeParseException e) {
            return false;
        }
    }


    /**
     * Formats the event data into a single line for disk storage.
     * We save the dates in the same format we expect during input to make
     * reloading the data seamless.
     *
     * @return A string formatted as "E | status | description | start | end".
     */


    @Override
    public String toFileFormat() {
        String fromStr = startDate.format(INPUT_FORMAT);
        String toStr = endDate.format(INPUT_FORMAT);
        return "E | " + (getStatus() ? "1" : "0") + " | " + description + " | " + fromStr + " | " + toStr;
    }

    /**
     * Returns a human-readable string of the event.
     * Converts the internal dates into a "Month Day Year" format for the UI.
     *
     * @return The formatted string (e.g., "[E][ ] Party (from: Jan 01 2024 to: Jan 02 2024)").
     */

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " +
                startDate.format(OUTPUT_FORMAT) + " to: " +
                endDate.format(OUTPUT_FORMAT) + ")";
    }
}
