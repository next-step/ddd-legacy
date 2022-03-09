package calculator.application;

import static calculator.application.StringCalculator.StringCalculatorDelimiter.delimiterSeparate;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import calculator.domain.PositiveNumber;

public class StringCalculator {

    public int add(final String text) {

        if (isNullOrEmpty(text)) {
            return PositiveNumber.getMinimumNumber();
        }

        String[] filteredNumberInText = delimiterSeparate(text);

        return Arrays.stream(stringToInt(filteredNumberInText))
                     .mapToObj(PositiveNumber::from)
                     .reduce(PositiveNumber.getZero(), PositiveNumber::add)
                     .getNumber();
    }

    private boolean isNullOrEmpty(final String text) {
        if (Objects.isNull(text)) {
            return true;
        }
        if (text.isEmpty()) {
            return true;
        }
        return false;
    }

    private int[] stringToInt(final String[] filteredNumberInText) {
        return Arrays.stream(filteredNumberInText)
                     .mapToInt(Integer::parseInt)
                     .toArray();
    }

    static final class StringCalculatorDelimiter {
        private static final String DEFAULT_DELIMITER = ",|:";
        private static final Pattern DEFAULT_COMPILE = Pattern.compile(DEFAULT_DELIMITER);
        private static final String CUSTOM_DELIMITER = "//(.)\n(.*)";
        private static final Pattern CUSTOM_COMPILE = Pattern.compile(CUSTOM_DELIMITER);

        private StringCalculatorDelimiter() {
        }

        static String[] delimiterSeparate(final String value) {
            Matcher matcher = CUSTOM_COMPILE.matcher(value);

            if (matcher.find()) {
                final String customDelimiter = matcher.group(1);
                return matcher.group(2).split(customDelimiter);
            }

            return DEFAULT_COMPILE.split(value);
        }
    }

}
