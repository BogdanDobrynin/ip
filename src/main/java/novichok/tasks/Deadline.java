package novichok.tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Deadline extends Task {
    private LocalDateTime by;
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    public Deadline(String description, String date) {
        super(description);
        this.by = LocalDateTime.parse(date, INPUT_FORMAT);
    }

    public boolean isOn(String date) {
        try {
            LocalDateTime targetDateTime = LocalDateTime.parse(date, INPUT_FORMAT);
            return this.by.toLocalDate().equals(targetDateTime.toLocalDate());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @Override
    public String toFileFormat() {
        // Save the date in the INPUT_FORMAT so it can be re-parsed easily later
        return "D | " + super.toFileFormat() + " | " + by.format(INPUT_FORMAT);
    }

    @Override
    public String toString() {
        DateTimeFormatter out = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");
        return "[D]" + super.toString() + " (by: " + by.format(out) + ")";
    }
}
