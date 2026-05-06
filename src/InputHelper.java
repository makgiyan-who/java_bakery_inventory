// ============================================================
// InputHelper.java
// Purpose : Handles all user input and validation
// Author  : Giane
// ============================================================

import java.util.Scanner;

public class InputHelper {

    // ----------------------------------------------------------
    // Single shared Scanner instance for the entire program.
    // We use ONE scanner to avoid resource conflicts.
    // All methods in this class use this same scanner.
    // ----------------------------------------------------------
    private static final Scanner scanner = new Scanner(System.in);


    // ==========================================================
    // METHOD 1: getInt()
    // Purpose : Reads a valid integer from the user
    // Params  : prompt  → message shown to user
    //           min     → minimum accepted value
    //           max     → maximum accepted value
    // Returns : a valid int within [min, max]
    // ==========================================================
    public static int getInt(String prompt, int min, int max) {

        int value = 0;
        boolean valid = false;

        while (!valid) {
            System.out.print(prompt);

            // Check if the input is actually an integer
            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                scanner.nextLine(); // flush leftover newline

                // Check if it falls within the allowed range
                if (value >= min && value <= max) {
                    valid = true;
                } else {
                    System.out.println(
                        "  [!] Input must be between " + min + " and " + max + ". Try again."
                    );
                }
            } else {
                // User typed letters or symbols instead of a number
                System.out.println("  [!] Invalid input. Please enter a whole number.");
                scanner.nextLine(); // discard the bad input
            }
        }

        return value;
    }


    // ==========================================================
    // METHOD 2: getDouble()
    // Purpose : Reads a valid decimal number (for price)
    // Params  : prompt → message shown to user
    //           min    → minimum accepted value
    // Returns : a valid double >= min
    // ==========================================================
    public static double getDouble(String prompt, double min) {

        double value = 0;
        boolean valid = false;

        while (!valid) {
            System.out.print(prompt);

            if (scanner.hasNextDouble()) {
                value = scanner.nextDouble();
                scanner.nextLine(); // flush leftover newline

                if (value >= min) {
                    valid = true;
                } else {
                    System.out.println(
                        "  [!] Value must be at least " + min + ". Try again."
                    );
                }
            } else {
                System.out.println("  [!] Invalid input. Please enter a valid number (e.g. 25.50).");
                scanner.nextLine(); // discard bad input
            }
        }

        return value;
    }


    // ==========================================================
    // METHOD 3: getString()
    // Purpose : Reads a non-empty string from the user
    // Params  : prompt    → message shown to user
    //           maxLength → maximum characters allowed
    // Returns : a trimmed, non-empty string
    // ==========================================================
    public static String getString(String prompt, int maxLength) {

        String value = "";
        boolean valid = false;

        while (!valid) {
            System.out.print(prompt);
            value = scanner.nextLine().trim();

            if (value.isEmpty()) {
                System.out.println("  [!] Input cannot be empty. Please try again.");

            } else if (value.length() > maxLength) {
                System.out.println(
                    "  [!] Input too long. Maximum " + maxLength + " characters allowed."
                );
            } else {
                valid = true;
            }
        }

        return value;
    }


    // ==========================================================
    // METHOD 4: getMenuChoice()
    // Purpose : Reads the user's menu selection
    //           Wraps getInt() specifically for menu navigation
    // Params  : totalOptions → how many menu items exist
    // Returns : valid menu choice integer
    // ==========================================================
    public static int getMenuChoice(int totalOptions) {
        return getInt("  Enter your choice: ", 1, totalOptions);
    }


    // ==========================================================
    // METHOD 5: getPositiveInt()
    // Purpose : Reads a positive integer (for stock/quantity)
    //           Minimum value is always 1
    // Params  : prompt → message shown to user
    // Returns : integer >= 1
    // ==========================================================
    public static int getPositiveInt(String prompt) {
        return getInt(prompt, 1, Integer.MAX_VALUE);
    }


    // ==========================================================
    // METHOD 6: getID()
    // Purpose : Reads an ID input from the user
    //           IDs must be positive integers starting at 1
    // Params  : prompt → message shown to user
    // Returns : integer >= 1
    // ==========================================================
    public static int getID(String prompt) {
        return getInt(prompt, 1, Integer.MAX_VALUE);
    }


    // ==========================================================
    // METHOD 7: confirm()
    // Purpose : Asks user to confirm an action (Y/N)
    //           Used before deleting or applying transactions
    // Params  : message → the confirmation question
    // Returns : true if user typed Y or y, false otherwise
    // ==========================================================
    public static boolean confirm(String message) {

        System.out.print("  " + message + " (Y/N): ");
        String input = scanner.nextLine().trim();
        return input.equalsIgnoreCase("Y");
    }


    // ==========================================================
    // METHOD 8: pressEnterToContinue()
    // Purpose : Pauses the program until user presses Enter
    //           Keeps the console readable between operations
    // ==========================================================
    public static void pressEnterToContinue() {
        System.out.print("\n  Press Enter to continue...");
        scanner.nextLine();
    }


    // ==========================================================
    // METHOD 9: closeScanner()
    // Purpose : Closes the Scanner when program exits
    //           Called once in Main.java before System.exit()
    // ==========================================================
    public static void closeScanner() {
        scanner.close();
    }
}