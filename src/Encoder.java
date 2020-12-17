/**
 * Class that is responsible for encoding and decoding information messages
 */
public class Encoder {
    private GaloisField field; // field that information messages are pulled from
    private String generator; // generator for both the field and the cyclic Hamming code

    /**
     * Constructor for an encoder object
     * @param generator, a binary string that represents a primitive polynomial over Z2 (most significant term on left)
     */
    public Encoder(String generator) {
        field = new GaloisField(generator);
        this.generator = generator;
    }

    /**
     * Encode a given message using a cyclic Hamming code, which can correct a single error
     * @param message, string of information that will be encoded
     * @return the encoded information string (each character will become a binary string)
     */
    public String encode(String message) {
        // gather message as a string of number tokens then convert them to binary strings
        String[] splitMessage = message.split(" ", 0);
        for(int i = 0; i < splitMessage.length; ++i) {
            int currNum = Integer.parseInt(splitMessage[i]);
            splitMessage[i] = Integer.toBinaryString(currNum);
        }

        // encode each binary string
        StringBuilder encodedMessage = new StringBuilder();
        for(String word : splitMessage) {
            String encodedWord = encodeWord(word) + " ";
            encodedMessage.append(encodedWord);
        }

        return encodedMessage.toString();
    }

    /**
     * Encode a single word of information using a cyclic Hamming code
     * @param word, word to be encoded
     * @return the encoded word
     */
    private String encodeWord(String word) {
        // encode each word by multiplying it with the generator
        int[] result = new int[word.length()  + generator.length() - 1];
        for(int i = 0; i < word.length(); ++i) {
            if(word.charAt(i) == '1') {
                int exp1 = word.length() - i - 1;
                for(int j = 0; j < generator.length(); ++j) {
                    if(generator.charAt(j) == '1') {
                        int exp2 = generator.length() - j - 1;
                        if (result[exp1 + exp2] == 1) {
                            result[exp1 + exp2] = 0;
                        } else {
                            result[exp1 + exp2] = 1;
                        }
                    }
                }
            }
        }

        // put result of multiplication into a string
        StringBuilder resultBuilder = new StringBuilder();
        boolean shouldAppend = false;
        for(int i = result.length - 1; i >= 0; --i) {
            if(!shouldAppend && result[i] == 1) {
                shouldAppend = true;
            }

            if(shouldAppend) {
                resultBuilder.append(Character.forDigit(result[i], 10));
            }
        }

        return resultBuilder.toString();
    }

    /**
     * decoded a given string of information that has previously been encoded
     * In a cyclic Hamming code, words are encoded by multiplying them with a generator polynomial
     * @param message, message to be decoded
     * @return the decoded message
     */
    public String decode(String message) {
        String[] splitMessage = message.split(" ", 0);
        StringBuilder decodedMessage = new StringBuilder();

        // decode every string of binary back into their decimal representation
        for(String word : splitMessage) {
            String decodedWord = decodeWord(word) + " ";
            decodedMessage.append(decodedWord);
        }

        return decodedMessage.toString();
    }

    /**
     * decode a single word of information using a cyclic Hamming code
     * In a cyclic Hamming code, words are decoded by dividing them by the generator polynomial
     * @param word, word to be decoded
     * @return the decoded word
     */
    private String decodeWord(String word) {
        String[] quotient = dividePolynomials(word, generator);

        // If the remainder is null, then an error occurred. The error location is equal to the exponent
        // the represents the remainder as an element of the field, so we find that exponent and then
        // flip the bit that is in that location and redivide
        if(quotient[1] != null) {
            String element = ("0".repeat(generator.length() - 1 - quotient[1].length())) + quotient[1];
            int elementExp = field.getExponent(element);

            StringBuilder correctedWord = new StringBuilder();
            for(int i = 0; i < quotient[0].length(); ++i) {
                if(quotient[0].length() - i - 1 == elementExp) {
                    if(quotient[0].charAt(i) == '1') {
                        correctedWord.append('0');
                    } else {
                        correctedWord.append('1');
                    }
                } else {
                    correctedWord.append(quotient[0].charAt(i));
                }
            }
            
            quotient = dividePolynomials(correctedWord.toString(), generator);
        }
        
        return Integer.toString(binaryToDecimal(quotient[0]));
    }

    /**
     * divide two given polynomials, which are represented as binary strings with the most significant term on the left
     * @param num, polynomial that is in the numerator
     * @param denom, polynomial that is in the denominator
     * @return String array, first element is the quotient and second element is the remainder (null if there is none)
     */
    private static String[] dividePolynomials(String num, String denom) {
        // if denominator is greater than numerator then it doesn't divide in
        if(denom.length() > num.length()) {
            return new String[]{null, num};
        }

        String[] quotientAndRemainder = new String[2];
        String newNum = num;
        int denomDegree = denom.length() - 1;
        int[] quotientArr = new int[newNum.length() - denomDegree];

        while(true) {
            // if denominator is bigger than numerator, it no longer divides in
            int numDegree = newNum.length() - 1;
            if(numDegree < denomDegree) {
                quotientAndRemainder[1] = newNum;
                break;
            }

            quotientArr[numDegree - denomDegree] = 1; // current term in quotient is x^(numDegree - denomDegree)

            // build the new numerator
            StringBuilder numBuilder = new StringBuilder();
            boolean shouldAppend = false;
            for(char bit : binaryAdd(newNum, denom + "0".repeat(numDegree - denomDegree)).toCharArray()) {
                if(!shouldAppend && bit == '1') {
                    shouldAppend = true;
                }

                if(shouldAppend) {
                    numBuilder.append(bit);
                }
            }

            // if the new numerator is 0, then we have finished the division with a remainder of zero
            newNum = numBuilder.toString();
            if(newNum.equals("0".repeat(newNum.length()))) {
                break;
            }
        }

        // build the quotient string out of the quotientArr
        StringBuilder quotient = new StringBuilder();
        for(int i = quotientArr.length - 1; i >= 0; --i) {
            quotient.append(Character.forDigit(quotientArr[i], 10));
        }
        quotientAndRemainder[0] = quotient.toString();

        return quotientAndRemainder;
    }

    /**
     * add two binary strings of the same size together
     * @param lhs, first added
     * @param rhs, second addend
     * @return sum of the two binary strings
     */
    private static String binaryAdd(String lhs, String rhs) {
        if(lhs.length() != rhs.length()) {
            return null;
        }

        // 1+1=0, 1+0=1, 0+1=1, and 0+0=0
        StringBuilder sum = new StringBuilder();
        for(int i = 0; i < lhs.length(); ++i) {
            if(lhs.charAt(i) == rhs.charAt(i)) {
                sum.append('0');
            } else {
                sum.append('1');
            }
        }

        return sum.toString();
    }

    /**
     * convert binary string to a decimal integer
     * @param binary, string that will be converted to integer
     * @return decimal representation of the binary string
     */
    private static int binaryToDecimal(String binary) {
        int decimalValue = 0;
        for(int i = 0; i < binary.length(); ++i) {
            if(binary.charAt(i) == '1') {
                decimalValue += (int) Math.pow(2, binary.length() - i - 1);
            }
        }

        return decimalValue;
    }
}
