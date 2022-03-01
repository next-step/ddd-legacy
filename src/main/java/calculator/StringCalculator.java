package calculator;

public final class StringCalculator {

    public int add(final String expression) {
        if (isBlank(expression)) {
            return 0;
        }
        try {
            return Integer.parseInt(expression);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private boolean isBlank(String expression) {
        return expression == null || expression.trim().length() == 0;
    }
}
