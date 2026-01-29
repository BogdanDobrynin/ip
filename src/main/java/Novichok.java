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
            if (exitKeywords.contains(userCommand.toLowerCase())) {
                System.out.println("\t-----------------------");
                System.out.println("\tBye. Hope to see you again soon!");
                System.out.println("\t-----------------------");
                break;
            } else if (userCommand.equalsIgnoreCase("list")) {
                System.out.println("\t-----------------------");
                taskManager.printList();
                System.out.println("\t-----------------------");
            } else {
                // command echo
                System.out.println("\t-----------------------");
                System.out.println("\tadded: " + userCommand);
                taskManager.addTask(userCommand);
                System.out.println("\t-----------------------\n");
            }
        }
    }
}
