package calculator;

import java.util.Arrays;

public class StringCalculator {
    private static final int ZERO_OF_STRING_CALCULATOR = 0;

    public int add(String text) {
        if (isNullValue(text)) {
            return ZERO_OF_STRING_CALCULATOR;
        }
        if (isSingleText(text)) {
            getNumeric(text);
        }

        return Arrays.stream(splittingText(text))
            .mapToInt(this::getNumeric)
            .sum();
    }

    private boolean isNullValue(String text) {
        return text == null || text.isEmpty();
    }

    private boolean isSingleText(String text) {
        return text.length() == 1;
    }

    private int getNumeric(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자가 아닌 값이 존재 합니다.");
        }
    }

    private String[] splittingText(String text) {
        return text.split(",");
    }

}
