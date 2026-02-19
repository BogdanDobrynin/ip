package novichok.ui;

import novichok.exceptions.NovichokException;

import java.util.Scanner;

public class Ui {
    private static final String DIVIDER = "════════════════════════════════════════════════════════════════════════════════════════════════";
    private final Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    public String readCommand() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine().trim();
        }
        return "";
    }

    public void printWelcomeMessage() {
        Logo.printLogo();

        String welcome = """
        
        Greetings, User.
        Novichok Task Protocol initialized. System is online.
        Type 'help' to see available commands or 'bye' to terminate.
        """;

        // speedMs: 15-20 gives a nice "computing" feel for the welcome message
        typewriterPrint(welcome, 15);

        // We can also animate the divider for a cool "scanning" effect
        typewriterPrint(DIVIDER + "\n", 2);
    }

    public void printMenu(int speed) {
        String menuContent = """
        NOVICHOK COMMAND MANUAL
       
        BASIC TASKS:
          todo [desc]                     - Add a simple task
          deadline [desc] /by [d/M/y HHmm]- Add task with a deadline (e.g., 19/2/2026 1800)
          event [desc] /from [t] /to [t]  - Add an event with start/end times
        
        MANAGEMENT:
          list                            - Display all recorded tasks
          mark [index1 index2...]         - Mark one or more tasks as completed
          unmark [index1 index2...]       - Mark one or more tasks as incomplete
          delete [index1 index2...]       - Remove tasks from the protocol
        
        ADVANCED:
          filter /name [keyword]          - Find tasks containing a specific word
          filter /date [d/M/y HHmm]       - Find tasks occurring on a specific date
          filter /type [todo/deadline/event] - Filter by task category
        
        SYSTEM:
          help                            - Show this manual
          bye | exit | quit               - Save changes and exit
        """;
        typewriterPrint(menuContent, speed);
        typewriterPrint(DIVIDER + "\n", 2);

    }

    public void showError(String errorMessage) {
        System.out.println(errorMessage);
    }

    public void printCustomMessage(String message) {
        System.out.println(message);
    }

    public void printDivider() {
        System.out.println(DIVIDER);
    }

    public void printExitMessage() {
        System.out.println("\tBye. Hope to see you again soon!");
    }

    /**
     * Prints a string with a typewriter effect.
     * @param text The string to print.
     * @param delay The delay in milliseconds between characters.
     */

    private void typewriterPrint(String text, int delay) {
        for (char c : text.toCharArray()) {
            System.out.print(c);
            System.out.flush();
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println();
    }
}
