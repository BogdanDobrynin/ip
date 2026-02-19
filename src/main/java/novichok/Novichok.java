package novichok;

import novichok.logic.TaskManager;
import novichok.ui.Ui;

import java.util.List;

public class Novichok {
    /**
     * Divider line used to separate bot responses for better readability.
     */

    public void run() {
        Ui ui = new Ui();
        TaskManager taskManager = new TaskManager("data/list.log", ui);
        // user input exit keywords
        List<String> exitKeywords = List.of("exit", "quit", "bye");

        ui.printWelcomeMessage();
        ui.printMenu(5);

        while (true) {
            String userCommand = ui.readCommand();
            if (userCommand.isEmpty()) {
                continue;
            }

            // Split the command into two parts: action + args
            String[] parts = userCommand.trim().split(" ", 2);
            String commandAction = parts[0].toLowerCase().trim();
            // allows for action only commands
            String commandArguments = (parts.length > 1) ? parts[1].trim() : "";

            ui.printDivider();
            if (exitKeywords.contains(commandAction)) {
                ui.printExitMessage();
                break;
            } else {
                taskManager.executeCommand(commandAction, commandArguments);
            }
            ui.printDivider();
        }
    }

    public static void main(String[] args) {
        new Novichok().run();
    }
}
