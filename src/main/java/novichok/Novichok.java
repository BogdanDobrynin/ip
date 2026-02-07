package novichok;

import novichok.logic.TaskManager;
import java.util.List;
import java.util.Scanner;

public class Novichok {
    /**
     * Divider line used to separate bot responses for better readability.
     */

    private static final String DIVIDER = "\t-----------------------";

    /**
     * Novichok logo branding
     */

    private static final String LOGO = """
             _   _           _      _            _
            | \\ | | _____  _(_) ___| |__   ___  | | __
            |  \\| |/ _\\ \\ / / |/ __| '_ \\ / _ \\ | |/ /
            | |\\  | (_)\\ V /| | (__| | | | (_) ||   <
            |_| \\_|\\___/\\_/ |_|\\___|_| |_|\\___/ |_|\\_\\
            """;

    public static void main(String[] args) {
        Scanner userScanner = new Scanner(System.in);
        TaskManager taskManager = new TaskManager();
        // user input exit keywords
        List<String> exitKeywords = List.of("exit", "quit", "bye");

        System.out.println(LOGO);
        System.out.println("Greetings user!\n");
        System.out.println("How can I serve you?");
        System.out.println("-----------------------");

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
}
