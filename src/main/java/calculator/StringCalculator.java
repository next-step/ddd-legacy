package calculator;

public class StringCalculator {
    public int add(String text) {

        if (isValidateString(text)) {
            return 0;
        }

        if (isOneLength(text)) {
            return Integer.parseInt(text);
        }

        return 1;

    }

    private boolean isOneLength(String text) {
        return text.length() == 1;
    }

    private boolean isValidateString(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }
        return false;

    }
}
