package kitchenpos.stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TextCalculateTypes implements CalculateFunction {
    NullOrEmpty {
        @Override
        public int calculate(String text) {
            return 0;
        }
    },
    SingleNumber {
        @Override
        public int calculate(String text) {
            int number = Integer.parseInt(text);
            return CalculateFunction.getValidatedNumber(number);
        }
    },
    SingleComma {
        @Override
        public int calculate(String text) {
            return Arrays.stream(text.split(","))
                    .map(Integer::parseInt)
                    .map(CalculateFunction::getValidatedNumber)
                    .reduce(0, Integer::sum);
        }
    },
    CommaAndColon {
        @Override
        public int calculate(String text) {
            return Arrays.stream(text.split(",|:"))
                    .map(Integer::parseInt)
                    .map(CalculateFunction::getValidatedNumber)
                    .reduce(0, Integer::sum);
        }
    },
    CustomDelimiter {
        @Override
        public int calculate(String text) {
            Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
            if(m.find()) {
                String customDelimiter = m.group(1);
                String[] tokens = m.group(2).split(customDelimiter);
                return Arrays.stream(tokens)
                        .map(Integer::parseInt)
                        .map(CalculateFunction::getValidatedNumber)
                        .reduce(0, Integer::sum);
            }

            throw new IllegalArgumentException();

        }
    },
    NotFound {
        @Override
        public int calculate(String text) throws RuntimeException {
            throw new IllegalArgumentException();
        }
    };

    public static TextCalculateTypes of(final String text) {
        if (text == null || text.isBlank()) {
            return NullOrEmpty;
        }
        if (isSingleNumber(text)) {
            return SingleNumber;
        }
        if (isSingleCommaType(text)) {
            return SingleComma;
        }
        if (isCommaAndColonType(text)) {
            return CommaAndColon;
        }
        if (isCustomDelimiter(text)) {
            return CustomDelimiter;
        }

        return NotFound;
    }

    private static boolean isSingleNumber(final String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isSingleCommaType(final String text) {
        final String[] tokens = text.split(",");

        if (tokens.length == 0) {
            return false;
        }

        try {
            Arrays.stream(tokens).forEach(Integer::parseInt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isCommaAndColonType(final String text) {
        String[] tokens = text.split(",|:");

        if (tokens.length == 0) {
            return false;
        }

        try {
            Arrays.stream(tokens).forEach(Integer::parseInt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isCustomDelimiter(final String text) {
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            return true;
        }

        return false;
    }


}
