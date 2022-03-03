package stringcalculator;

public class StringCalculator {

    private static final int SINGLE_STRING_LENGTH = 1;

    private String source;

    public StringCalculator(String source) {
        this.source = source;
    }

    public int add() {
        if (isNullOrEmpty(source)) {
            return 0;
        }

        if (isSingleLength(source)) {
            return 0;
        }

        return 0;
    }

    private boolean isNullOrEmpty(String source) {
        return source == null || source.isEmpty();
    }

    private boolean isSingleLength(String input) {
        return input.length() == SINGLE_STRING_LENGTH;
    }

}
