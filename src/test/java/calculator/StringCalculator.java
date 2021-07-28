package calculator;

public class StringCalculator {

    private static final int ZERO = 0;

    public int add(final String text) {
        if (isNullOrEmpty(text)) {
            return ZERO;
        }
        return Integer.parseInt(text);
    }

    private boolean isNullOrEmpty(final String text) {
        return text == null || text.isEmpty();
    }
}
