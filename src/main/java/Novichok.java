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
            // user's action
            String userAction = userCommand.split(" ")[0].toLowerCase(); // Store it once

            // exit condition check
            System.out.println(DIVIDER);
            if (exitKeywords.contains(userAction)) {
                System.out.println("\tBye. Hope to see you again soon!");
                break;
            } else if (userAction.equals("list")) {
                taskManager.printList();
            } else if (userAction.equalsIgnoreCase("mark") ||
                    userAction.equalsIgnoreCase("unmark")) {
                taskManager.taskStatusUpdate(userCommand);
            } else {
                // command echo
                System.out.println("\tadded: " + userCommand);
                taskManager.addTask(userCommand);
            }
            System.out.println(DIVIDER);
        }
    }
}
