package stringcalculator;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final String SPLIT_COMMA_OR_COLON = ",|:";
    private static final Pattern CUSTOM_SPLIT_PATTERN = Pattern.compile("//(.)\n(.*)");


    public int add(final String text) {
        if (validateNullOrEmpty(text)) {
            return ZERO;
        }

        if (validationIsOneNumber(text)) {
            return parseStringToInt(text);
        }

        return addCustomSplitNumbers(text);
    }

    private boolean validateNullOrEmpty(final String text) {
        return Objects.isNull(text) || text.isBlank();
    }

    private boolean validationIsOneNumber(final String text) {
        return text.length() == ONE;
    }

    private int parseStringToInt(final String text) {
        try {
            return parseInt(text);
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("숫자가 아닙니다.");
        }
    }

    private boolean validateNegativeInt(final int number) {
        return number < ZERO;
    }

    private int parseInt(final String text) {
        final int parseInt = Integer.parseInt(text);

        if (validateNegativeInt(parseInt)) {
            throw new RuntimeException("음수는 입력 불가능합니다.");
        }

        return parseInt;
    }

    private int addSplitNumbers(final String text, final String Delimiter) {
        return Arrays.stream(text.split(Delimiter))
            .mapToInt(this::parseStringToInt)
            .sum();
    }

    private int addCustomSplitNumbers(final String text) {
        final Matcher matcher = CUSTOM_SPLIT_PATTERN.matcher(text);

        if (matcher.find()) {
            return addSplitNumbers(matcher.group(TWO), matcher.group(ONE));
        }

        return addSplitNumbers(text, SPLIT_COMMA_OR_COLON);
    }
}
