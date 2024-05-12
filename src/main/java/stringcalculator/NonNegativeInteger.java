package stringcalculator;

import java.util.List;

public class NonNegativeInteger {
    private static final int INTEGER_ZERO = 0;
    private static final String NEGATIVE_ERROR_MESSAGE = "Negative integer found: ";
    private static final String NON_INTEGER_ERROR_MESSAGE = "Non integer found: ";
    private final int integer;

    private NonNegativeInteger(int integer) {
        this.integer = integer;
    }

    public static NonNegativeInteger of(String token) {
        try {
            int integer = Integer.parseInt(token);
            if (integer < INTEGER_ZERO) {
                throw new RuntimeException(NEGATIVE_ERROR_MESSAGE + integer);
            }
            return new NonNegativeInteger(integer);
        } catch (NumberFormatException e) {
            throw new RuntimeException(NON_INTEGER_ERROR_MESSAGE + token);
        }
    }

    public static NonNegativeInteger sum(List<NonNegativeInteger> nonNegativeIntegers) {
        int total = INTEGER_ZERO;
        for (NonNegativeInteger integer : nonNegativeIntegers) {
            total += integer.getInteger();
        }
        return new NonNegativeInteger(total);
    }

    public int getInteger() {
        return integer;
    }
}