package calculator;

public class StringSplitStrategy implements SplitStrategy {

    @Override
    public String[] split(String input) {
        if (isNullAndEmpty(input)) {
            return new String[] {"0"};
        }
        if (isInputOnlyOne(input)) {
            return new String[] {input};
        }
        return input.split(",|:");
    }

    private boolean isNullAndEmpty(String input) {
        return input == null || input.isEmpty();
    }

    private boolean isInputOnlyOne(String input) {
        return input.length() == 1;
    }
}