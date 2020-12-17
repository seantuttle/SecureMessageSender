import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Encrypter encrypter = new Encrypter();
        Encoder encoder = new Encoder("10000011");
        Scanner scnr = new Scanner(System.in);

        introduction();

        do {
            String message = getUserMessage(scnr);

            // encrypt the message
            message = encrypter.encrypt(message);
            System.out.println("\nThis is the encrypted message...\n" + message);

            // encode the message
            message = encoder.encode(message);
            System.out.println("\nThis is the encrypted and encoded message...\n" + message);

            // decode the message
            message = encoder.decode(message);
            System.out.println("\nThis is the encrypted and decoded message...\n" + message);

            // decrypt the message
            message = encrypter.decrypt(message);
            System.out.println("\nThis is the decrypted and decoded message...\n" + message);
        } while (getShouldContinue(scnr));
    }

    private static void introduction() {
        System.out.println("Welcome to the Secure Message \"Sender\"");
        System.out.println("You will enter a message and it will be encoded and encrypted");
        System.out.println("Legal characters are letters (uppercase and lowercase), numbers, spaces, and " +
                "those in the set {'.', ',', '?', '!'}");
    }

    private static String getUserMessage(Scanner scnr) {
        String message;

        System.out.println("\nEnter your message...");
        message = scnr.nextLine();

        return message;
    }

    private static boolean getShouldContinue(Scanner scnr) {
        System.out.print("\nDo you want to send another message (y/n)? ");
        String shouldContinue = scnr.nextLine().toLowerCase();

        return shouldContinue.charAt(0) == 'y';
    }
}
