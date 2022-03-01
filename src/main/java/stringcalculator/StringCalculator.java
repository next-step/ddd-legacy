package stringcalculator;

public class StringCalculator {

    private static final int EMPTY_NUMBER = 0;
    private static final int SINGLE_INPUT_LENGTH = 1;
    private static final String TOKEN_DELIMITER = ",|:";

    public int add(String text) {
        if (text == null  || text.isEmpty()) {
            return EMPTY_NUMBER;
        }
        if (text.length() == SINGLE_INPUT_LENGTH && isInteger(text)) {
            return Integer.parseInt(text);
        }
        return sum(text.split(TOKEN_DELIMITER));
    }

    private boolean isInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int sum(String[] tokens) {
        int sum = EMPTY_NUMBER;
        for (String token : tokens) {
            sum+=Integer.parseInt(token);
        }
        return sum;
    }
}
