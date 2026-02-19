package novichok.tasks;

import novichok.exceptions.NovichokException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Event extends Task {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    // Use a static formatter to avoid creating a new one for every object
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

    public Event(String description, String startDate, String endDate) throws NovichokException {
        super(description);
        this.startDate = LocalDateTime.parse(startDate, INPUT_FORMAT);
        this.endDate = LocalDateTime.parse(endDate, INPUT_FORMAT);
        if (this.startDate.isAfter(this.endDate)) {
            throw new NovichokException("Error: Start time cannot be after end time. Time travel not supported!");
        }
    }

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

    @Override
    public String toFileFormat() {
        String fromStr = startDate.format(INPUT_FORMAT);
        String toStr = endDate.format(INPUT_FORMAT);
        return "E | " + (getStatus() ? "1" : "0") + " | " + description + " | " + fromStr + " | " + toStr;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " +
                startDate.format(OUTPUT_FORMAT) + " to: " +
                endDate.format(OUTPUT_FORMAT) + ")";
    }
}
