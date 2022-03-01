package stringcalculator;

public class StringCalculator {

    private static final int EMPTY_NUMBER = 0;

    public int add(String text) {
        if (text == null  || text.isEmpty()) {
            return EMPTY_NUMBER;
        }
        throw new IllegalArgumentException();
    }
}
