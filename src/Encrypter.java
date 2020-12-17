/**
 * Class that is responsible for encrypting and decrypting information messages
 * The values for N, E, and D are toy values that I chose because they pose no risk of accidental overflow
 * To change these values, the following rules must be acknowledged
 * 1. N must be the product of two distinct prime numbers, let's say P and Q
 * 2. E must be such that gcd(E, (P-1)*(Q-1))=1 and 1<E<(P-1)*(Q-1) (easiest to just a pick prime)
 * 3. D must be such that E*D mod (P-1)*(Q-1)=1, can find using extended Euclidean algorithm with E and (P-1)*(Q-1)
 */
public class Encrypter {
    private static final int N = 478783; // N = P*Q = 683*701
    private static final int E = 13963 ; // it must be that gcd(E, 682*700) = 1 and E < 682*700
    private static final int D = 70227; // E*D % 682*700 = 1
    private static final char[] CHAR_MAP = createCharMap();

    /**
     * create the character map that transforms characters to integersand vice versa
     * map contains all uppercase and lowercase letters, numbers, the space, and the set {'.', ',', '?', '!'}
     * @return the character map represented as an array (the indices are the associated integers)
     */
    private static char[] createCharMap() {
        char[] chars = new char[67];

        // add all lowercase letters in spots 0-25 and all uppercase letters in spots 26-51
        int index = 0;
        for(char ch = 'a'; ch <+ 'z'; ++ch) {
            chars[index] = ch;
            chars[index + 26] = Character.toUpperCase(ch);
            ++index;
        }

        // add all number characters in spots 52-61
        index = 52;
        for(char i = '0'; i <= '9'; ++i) {
            chars[index] = i;
            ++index;
        }

        // add the punctuation and space characters in spots 62-66
        chars[62] = ' ';
        chars[63] = '.';
        chars[64] = ',';
        chars[65] = '?';
        chars[66] = '!';

        return chars;
    }

    /**
     * encrypt the given string of information using the RSA scheme
     * @param message, string of information to be encrypted
     * @return the encrypted string of information (each character will be represented as an integer)
     */
    public String encrypt(String message) {
        StringBuilder encryptedMessage = new StringBuilder();

        // encrypt every character in the message; each will be represented by a character
        for(int i = 0; i < message.length(); ++i) {
            String currEncryptedChar = rsa(charToInt(message.charAt(i)), E) + " "; // E for encryption
            encryptedMessage.append(currEncryptedChar);
        }

        return encryptedMessage.toString();
    }

    /**
     * decrypt the previously encrypted string of information using the RSA scheme
     * @param message, previously encrypted string of information
     * @return the decrypted string of information
     */
    public String decrypt(String message) {
        String[] splitMessage = message.split(" ", 0);
        StringBuilder decryptedMessage = new StringBuilder();

        // decrypt every number back into its corresponding character
        for(String num : splitMessage) {
            decryptedMessage.append(intToChar(rsa(Integer.parseInt(num), D))); // D for decryption
        }

        return decryptedMessage.toString();
    }

    /**
     * RSA scheme, which is essentially raising a number to a large exponent, then modding by a large number.
     * This can be accomplished efficiently using a square and multiply algorithm
     * @param num, number to be encrypted or decrypted
     * @param exp, power that num will be raised to, will be E for encryption and D for decryption
     * @return the encrypted/decrypted number
     */
    private int rsa(int num, int exp) {
        String binary = Integer.toBinaryString(exp);
        long result = 1; // result is a long to keep overflow from happening mid computation

        // square result every iteration, multiply it by the original number for every 1 in the
        // in the binary representation of the exponent
        for(int i = 0; i < binary.length() - 1; ++i) {
            if(binary.charAt(i) == '1') {
                result = (result * num) % N;
            }
            result = (result * result) % N;
        }

        // if the exponent is odd, multiply the result by the original number one final time
        if(exp % 2 == 1) {
            result = (result * num) % N;
        }

        return (int) result;
    }

    /**
     * convert a character into its associated integer using the character map
     * Add 2 because 0 and 1 do not work in the RSA scheme (0^n=0 and 1^n=1), so we want to start at 2
     * @param ch, character that will be mapped to integer
     * @return the associated integer
     */
    private static int charToInt(char ch) {
        for(int i = 0; i < CHAR_MAP.length; ++i) {
            if(ch == CHAR_MAP[i]) {
                return i + 2;
            }
        }

        return -1;
    }

    /**
     * convert an integer into its associated character using the character map
     * Subtract two to counteract the added two seen in charToInt
     * @param num, integer that will be mapped to character
     * @return the associated character
     */
    private static char intToChar(int num) {
        return CHAR_MAP[num - 2];
    }
}
