package stringcalculator;

import org.junit.platform.commons.util.StringUtils;

public class StringCalculator {

    private static final int ZERO = 0;

    public int add(String text) {

        if(StringUtils.isBlank(text)) {
            return ZERO;
        }

        Numbers numbers = TextSplitter.split(text);

        if(numbers.hasAnyNegativeNumber()) {
            throw new RuntimeException();
        }

        if(numbers.hasOnlyOneNumber()) {
            return Integer.parseInt(text);
        }

        return numbers.addAll();
    }
}
