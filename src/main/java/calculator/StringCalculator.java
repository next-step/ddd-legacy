package calculator;

public class StringCalculator {
    public int add(String text) {

        if (isValidateString(text)) {
            return 0;
        }
        return 1;

    }

    private boolean isValidateString(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }
        return false;

    }
}
