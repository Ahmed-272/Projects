/**
 * Group 6 Members: Ahmed Mohamed, Abdulrazak Hassan
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;
import java.security.MessageDigest;

public class DefendCode {

    private static int[] numbers = new int [2];

    private static String name = null;

    private static String outputFileName = null;

    private static String inputFileName = null;

    private static Scanner console = new Scanner(System.in);

    private static Logger logger = Logger.getLogger(DefendCode.class.getName());

    /**
     * Ensured all input is properly obtained before proceeding to the next step in the program flow.
     * @param args
     * @throws IOException
     */

    public static void main(String[] args) throws IOException {
        FileHandler file = new FileHandler("error.log");
        logger.addHandler(file);
        SimpleFormatter format = new SimpleFormatter();
        file.setFormatter(format);

        try {
            getName();
            getInputFile();
            getOutputFile();
            getInt();
            getPassword();
            writeOutputToFile();
        } catch (IOException e) {
            logger.severe("IOException occurred: " + e.getMessage());
        } finally {
            file.close();
            console.close();
        }
    }

    /**
     * Implemented input validation to ensure that user inputs for first and last name are within
     * the specified length and contain only valid characters.
     */
    private static void getName() {
        System.out.println("Enter your first name (at most 10 characters, no special characters, " +
                "using only letters (A-Z, a-z)): ");
        String firstName = console.nextLine();

        while (firstName.length() > 10 || !regexName(firstName)) {
            System.out.println("Invalid input. Enter your first name again: ");
            firstName = console.nextLine();
        }

        System.out.println("Enter your last name (at most 10 characters, no special characters, " +
                "using only letters (A-Z, a-z)): ");
        String lastName = console.nextLine();
        while (lastName.length() > 10 || !regexName(lastName)) {
            System.out.println("Invalid input. Enter your last name again: ");
            lastName = console.nextLine();
        }
        System.out.println("First Name: " + firstName);
        System.out.println("Last Name: " + lastName);
        name = firstName +" " + lastName;
    }

    private static boolean regexName(String s) {
        Pattern pattern = Pattern.compile("^[a-zA-Z ]+$");
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }

    /**
     * Checked for valid input when prompting for integer values,
     * ensuring they fall within the range of a 4-byte integer.
     */
    private static void getInt() {
        int input1;
        int input2;
        System.out.println("Enter the first integer value (4-byte int range, between -2,147,483,648 and 2,147,483,647): ");

        while (!console.hasNextInt()) {
            System.out.println("Error: Please enter a valid integer.");
            console.nextLine();
        }
        input1 = console.nextInt();

        System.out.println("Enter the second integer value (4-byte int range, between -2,147,483,648 and 2,147,483,647): ");
        while (!console.hasNextInt()) {
            System.out.println("Error: Please enter a valid integer.");
            console.nextLine();
        }
        input2 = console.nextInt();

        System.out.println("First Integer: " + input1);
        System.out.println("Second Integer: " + input2);
        numbers = new int[]{input1, input2};
    }



    private static boolean validInput(int value) {
        return value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE;
    }
    private static boolean isValidFileName(String fileName) {
        Pattern pattern = Pattern.compile("^.+\\.txt$");
        Matcher matcher = pattern.matcher(fileName);
        return matcher.matches();
    }

    /**
     * Validated input file names to ensure they end with '.txt' and exist in the file system.
     */
    private static void getInputFile() {
        boolean isValidFile = false;
        do {
            System.out.println("Enter the name of your input file (.txt extension required): " +
                    "(A-Z, a-z) white spaces allowed. File must be in the same folder as this code ");
            while (!console.hasNextLine()) {
                System.out.println("File name is empty or to long try again.");
                console.nextLine();
            }
            inputFileName = console.nextLine();
            File inputFile = new File(inputFileName);
            if (isValidFileName(inputFileName) && inputFile.exists()) {
                isValidFile = true;
            } else {
                System.out.println("Invalid file name or file does not exist. Please make sure it ends with '.txt'. Try again.");
            }
        } while (!isValidFile);
    }

    /**
     * Validated output file names to ensure they end with '.txt' and exist in the file system.
     */
    private static void getOutputFile() {
        do {
            System.out.println("Enter the name of your output file (.txt extension required): " +
                    "(A-Z, a-z) white spaces allowed.");
            while (!console.hasNextLine()) {
                System.out.println("File name is empty, try again.");
                console.nextLine();
            }
            outputFileName = console.nextLine();
            if (!isValidFileName(outputFileName)) {
                System.out.println("Invalid file name. Please make sure it ends with '.txt'. Try again.");
            }
        } while (!isValidFileName(outputFileName));
}

    /**
     * Implemented proper password requirements,
     * ensuring passwords are at most 10 characters long and not zero characters.
     * @throws IOException
     */
    private static void getPassword() throws IOException {
        console.nextLine();
        String password;
        do {
            System.out.print("Enter password (at most 10 characters), letters (A-Z, a-z) special characters are allowed: ");
            password = console.nextLine();
            if (password.length() > 10 ||password.length() <= 0) {
                System.out.println("Password must be at most 10 characters long. Please try again.");
            }
        } while (password.length() > 10 ||password.length() <= 0);

        byte[] salt = generateSalt();
        String hashedPassword = getSecurePassword(password, salt);
        storeFile(outputFileName, hashedPassword);
        checkPassword(hashedPassword, salt);
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private static void storeFile(String filePath, String hashedPassword) throws IOException {
        try (FileWriter myWriter = new FileWriter(filePath)) {
            myWriter.write(hashedPassword);
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    private static String getSecurePassword(String password, byte[] salt) {
        String temp = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] bytes = md.digest(password.getBytes());
            temp = bytesToHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return temp;
    }
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static void checkPassword(String storedHash, byte[] salt) throws IOException {
        System.out.print("Enter password again for verification: ");
        String password = console.nextLine();
        String hashedPassword = getSecurePassword(password, salt);

        if (hashedPassword.equals(storedHash)) {
            System.out.println("Password verified successfully!");
        } else {
            System.out.println("Password verification failed. Please try again.");
            checkPassword(storedHash, salt);
        }
    }

    /**
     * Closed the Scanner object to prevent resource leaks.
     */
    private static void writeOutputToFile() {
        try (FileWriter outputFile = new FileWriter(outputFileName)) {
            outputFile.write("User's Name: " + name + "\n");

            outputFile.write("First Integer: " + numbers[0] + "\n");
            outputFile.write("Second Integer: " + numbers[1] + "\n");
            int sum;
            try {
                sum = numbers[0] + numbers[1];
                outputFile.write("Sum of Integers: " + sum + "\n");
            } catch (ArithmeticException e) {
                outputFile.write("Sum of Integers: Overflow occurred\n");
            }
            int product;
            try {
                product = numbers[0] * numbers[1];
                outputFile.write("Product of Integers: " + product + "\n");
            } catch (ArithmeticException e) {
                outputFile.write("Product of Integers: Overflow occurred\n");
            }

            outputFile.write("Input File Name: " + inputFileName + "\n");
            outputFile.write("Input File Contents:\n");

            try (Scanner sc = new Scanner(new File(inputFileName))) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    outputFile.write(line + "\n");
                }
                System.out.println("Successfully wrote to the output file.");
            } catch (IOException e) {
                System.out.println("An error occurred while writing to the output file.");
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}