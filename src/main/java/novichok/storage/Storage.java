package novichok.storage;

import novichok.tasks.Task;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
}
