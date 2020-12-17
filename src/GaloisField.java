/**
 * Class that represents a Galois Field of size 2^n (also called a finite field)
 */
public class GaloisField {
    private String[] field; // array of binary strings ordered such that they're a field

    /**
     * constructor for a Galois Field
     * @param generator, primitive polynomial of degree n will generate a Galois Field of size 2^n
     */
    public GaloisField(String generator) {
        field = new String[(int) Math.pow(2, generator.length() - 1)];

        field[0] = "0".repeat(generator.length() - 1);
        field[1] = "0".repeat(generator.length() - 2) + "1";

        // build each element by shifting one to the left, and flipping the two least significant bits
        // every time a one is shifted from being the most significant bit
        String prevElement = field[1];
        for (int i = 2; i < (int) Math.pow(2, generator.length() - 1); ++i) {
            StringBuilder element = new StringBuilder();
            for (int j = 1; j < prevElement.length(); ++j) {
                if (j < prevElement.length() - 1 || prevElement.charAt(0) == '0') {
                    element.append(prevElement.charAt(j));
                } else {
                    if (prevElement.charAt(prevElement.length() - 1) == '1') {
                        element.append('0');
                    } else {
                        element.append('1');
                    }
                }
            }

            if (prevElement.charAt(0) == '1') {
                element.append('1');
            } else {
                element.append('0');
            }

            field[i] = element.toString();
            prevElement = field[i];
        }
    }

    /**
     * get the field property
     * @return the string array representing the field
     */
    public String[] getField() {
        return field;
    }

    /**
     * get a specific element from the field, add one because the first element is considered to have index -1
     * @param exp, exponent (or index) representing the desired field element
     * @return the field element
     */
    public String getElement(int exp) {
        return field[exp + 1];
    }

    /**
     * get the exponent (or index) that represents the given field element
     * @param element, field element that you want to know the exponent for
     * @return the exponent that represents the given field element
     */
    public int getExponent(String element) {
        for (int i = 0; i < field.length; ++i) {
            if (field[i].equals(element)) {
                return i - 1;
            }
        }

        return -1;
    }

    /**
     * add two field elements together
     * @param exp1, the exponent that represents the first given field element
     * @param exp2, the exponent that represents the second given field element
     * @return the sum of the two field elements
     */
    public int add(int exp1, int exp2) {
        // an exponent of -1 means the element is zero, and 0+A=A
        if (exp1 == -1) {
            return exp2;
        } else if (exp2 == -1) {
            return exp1;
        }

        String element1 = getElement(exp1);
        String element2 = getElement(exp2);
        StringBuilder sum = new StringBuilder();

        // 1+1=0, 1+0=1, 0+1=1, and 0+0=0
        for (int i = 0; i < element1.length(); ++i) {
            if (element1.charAt(i) == element2.charAt(i)) {
                sum.append('0');
            } else {
                sum.append('1');
            }
        }

        return getExponent(sum.toString());
    }

    /**
     * multiply two field elements together
     * @param exp1, the exponent that represents the first given field element
     * @param exp2, the exponent that represents the second given field element
     * @return the product of the two field elements
     */
    public int multiply(int exp1, int exp2) {
        // an exponent of -1 means the element is zero, and 0*A=0
        if (exp1 == -1 || exp2 == -1) {
            return -1;
        }

        // These fields are cyclic, so modding by field.length-1 keeps the exponent in the right bounds
        return (exp1 + exp2) % (field.length - 1);
    }

    /**
     * raise a field element to the given power
     * @param exp, exponent representing the given field element
     * @param pow, power that the field element will be raised to
     * @return the field element raised to the given power
     */
    public int exponentiate(int exp, int pow) {
        // exp=-1 represents element zero and exp=0 represents element one, and 0^n=0, 1^n=1, and A^0=1
        if (exp == -1 || exp == 0) {
            return exp;
        } else if (pow == 0) {
            return 0;
        }

        int product = exp;
        for (int i = 0; i < pow - 1; ++i) {
            product = multiply(product, exp);
        }

        return product;
    }
}
