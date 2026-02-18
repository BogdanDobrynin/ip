package novichok.ui;

public class Logo {
    private static final String PADDING = "                ";
    private static final String TOP_LINE = PADDING + "╔═══════════════════════════════════════════════╗";
    private static final String BOTTOM_LINE = PADDING + "╚═══════════════════════════════════════════════╝";

    private static final String NOVICHOK_ASCII = """
             _   _           _      _            _    \s
            | \\ | | _____  _(_) ___| |__   ___  | | __\s
            |  \\| |/ _\\ \\ / / |/ __| '_ \\ / _ \\ | |/ /\s
            | |\\  | (_)\\ V /| | (__| | | | (_) ||   < \s
            |_| \\_|\\___/\\_/ |_|\\___|_| |_|\\___/ |_|\\_\\\s
            """;
    /**
     * Prints the Novichok brand logo to the console.
     */
    public static void printLogo() {
        System.out.println(TOP_LINE);

        String[] lines = NOVICHOK_ASCII.split("\n");
        for (String line : lines) {
            System.out.println(PADDING + "║  " + line + "  ║");
        }
        System.out.println(PADDING + "║            Your Personal Task Chemist         ║");
        System.out.println(BOTTOM_LINE);
        System.out.println();
    }
}