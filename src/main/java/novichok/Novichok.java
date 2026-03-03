package novichok;

import novichok.logic.TaskManager;
import novichok.ui.Ui;

import java.util.List;

public class Novichok {

    /**
     * The run method kicks off the main application loop.
     * It handles the initial greetings, reads what the user types, splits those
     * inputs into commands and arguments, and then sends them off to be processed.
     *
     * The loop keeps spinning until the user types an exit keyword like "bye" or "quit".
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
