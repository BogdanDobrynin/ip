package novichok.storage;

import novichok.exceptions.NovichokException;
import novichok.tasks.Deadline;
import novichok.tasks.Event;
import novichok.tasks.Task;
import novichok.tasks.ToDo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private final String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    public void saveListToDisk(List<Task> tasks) throws IOException {
        File listLog = new File(filePath);
        File parentDir = listLog.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            boolean isCreated = parentDir.mkdirs();
            if (!isCreated) {
                System.err.println("Warning: Could not create directories!");
            }
        }
        try (FileWriter writer = new FileWriter(listLog)) { // Overwrites old save with new list
            for (Task t : tasks) {
                writer.write(t.toFileFormat() + "\n");
            }
        }
    }

    public List<Task> loadListFromDisk() throws IOException, NovichokException {
        List<Task> loadedTasks = new ArrayList<>();
        Path path = Paths.get(filePath);

        // If the file doesn't exist, return an empty list instead of crashing
        if (!Files.exists(path)) {
            return loadedTasks;
        }

        List<String> lines = Files.readAllLines(path);
        for (String line : lines) {
            Task task = parseTaskFromLine(line);
            if (task != null) {
                loadedTasks.add(task);
            }
        }
        return loadedTasks;
    }

    private Task parseTaskFromLine(String line) throws NovichokException {
        // Split by the pipe character (escaped because | is a regex special character)
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) return null;

        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        Task task = null;
        try {
            switch (type) {
                case "T":
                    task = new ToDo(description);
                    break;
                case "D":
                    // parts[3] should be the deadline date string
                    if (parts.length >= 4) {
                        task = new Deadline(description, parts[3]);
                    }
                    break;
                case "E":
                    // parts[3] is 'from', parts[4] is 'to'
                    if (parts.length >= 5) {
                        task = new Event(description, parts[3], parts[4]);
                    }
                    break;
            }
        } catch (NovichokException | java.time.format.DateTimeParseException e) {
            System.err.println("Skipping corrupted task entry: " + line);
            return null;
        }
        if (task != null) {
            task.setStatus(isDone);
        }
        return task;
    }
}
