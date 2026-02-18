package novichok;

import novichok.logic.TaskManager;
import novichok.ui.Logo;

import java.util.List;
import java.util.Scanner;

public class Novichok {
    /**
     * Divider line used to separate bot responses for better readability.
     */
    private static final String DIVIDER = "════════════════════════════════════════════════════════════════════════════════════════════════";

    public void run() {
        Scanner userScanner = new Scanner(System.in);
        TaskManager taskManager = new TaskManager("data/list.log");
        // user input exit keywords
        List<String> exitKeywords = List.of("exit", "quit", "bye");

        Logo.printLogo();
        System.out.println("Greetings user!\n");
        System.out.println("How can I serve you?");
        System.out.println(DIVIDER);

        while (true) {
            // input acquisition and simple sanitization
            String userCommand = userScanner.nextLine().trim();
            if (userCommand.isEmpty()) {
                continue;
            }
            // Split the command into two parts: action + args
            String[] parts = userCommand.split(" ", 2);

            String commandAction = parts[0].toLowerCase().trim();
            // allows to parse action-only commands
            String commandArguments = "";

            // add the second element, if present
            if (parts.length > 1) {
                commandArguments = parts[1].trim();
            }

            System.out.println(DIVIDER);
            if (exitKeywords.contains(commandAction)) {
                System.out.println("\tBye. Hope to see you again soon!");
                break;
            } else {
                taskManager.executeCommand(commandAction, commandArguments);
            }
            System.out.println(DIVIDER);
        }
    }

    public static void main(String[] args) {
        new Novichok().run();
    }
}
