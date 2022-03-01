package stringcalculator;

public class StringCalculator {

    private static final int EMPTY_NUMBER = 0;
    private static final int SINGLE_INPUT_LENGTH = 1;

    public int add(String text) {
        if (text == null  || text.isEmpty()) {
            return EMPTY_NUMBER;
        }
        if (text.length() == SINGLE_INPUT_LENGTH && isInteger(text)) {
            return Integer.parseInt(text);
        }
        throw new IllegalArgumentException();
    }

    private boolean isInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
