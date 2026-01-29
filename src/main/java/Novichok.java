import java.util.List;
import java.util.Scanner;

public class Novichok {
    public static void main(String[] args) {
        Scanner userScanner = new Scanner(System.in);
        TaskManager taskManager = new TaskManager();
        // user input exit keywords
        List<String> exitKeywords = List.of("exit", "quit", "bye");

        String logo = """
             _   _           _      _            _
            | \\ | | _____  _(_) ___| |__   ___  | | __
            |  \\| |/ _\\ \\ / / |/ __| '_ \\ / _ \\ | |/ /
            | |\\  | (_)\\ V /| | (__| | | | (_) ||   <
            |_| \\_|\\___/\\_/ |_|\\___|_| |_|\\___/ |_|\\_\\
            """;
        System.out.println(logo);
        System.out.println("Greetings user!\n");
        System.out.println("How can I serve you?");
        System.out.println("-----------------------\n");


        while (true) {
            // input acquisition and simple sanitization
            String userCommand = userScanner.nextLine().trim();

            // exit condition check
            System.out.println("\t-----------------------");
            if (exitKeywords.contains(userCommand.toLowerCase())) {
                System.out.println("\tBye. Hope to see you again soon!");
                break;
            } else if (userCommand.equalsIgnoreCase("list")) {
                taskManager.printList();
            } else if (userCommand.split(" ")[0].contains("mark")) {
                taskManager.taskStatusUpdate(userCommand);
            } else {
                // command echo
                System.out.println("\tadded: " + userCommand);
                taskManager.addTask(userCommand);
            }
            System.out.println("\t-----------------------");
        }
    }
}
