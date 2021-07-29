package kitchenpos.stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TextCalculateType {
    NullOrEmpty(text -> Constants.EMPTY_OR_NULL_NUMBER),
    SingleNumber(
            text -> {
                ParsedNumber parsedNumber = new ParsedNumber(text);
                return parsedNumber.getNumber();
            }
    ),
    CommaAndColon(
            text -> Arrays.stream(text.split(Constants.COMMA_AND_COLON_REGEX))
                    .map(ParsedNumber::new)
                    .map(ParsedNumber::getNumber)
                    .reduce(0, Integer::sum)
    ),
    CustomDelimiter(
            text -> {
                Matcher m = Pattern.compile(Constants.CUSTOM_DELIMITER_REGEX).matcher(text);
                if (m.find()) {
                    String customDelimiter = m.group(1);
                    String[] tokens = m.group(2).split(customDelimiter);
                    return Arrays.stream(tokens)
                            .map(ParsedNumber::new)
                            .map(ParsedNumber::getNumber)
                            .reduce(0, Integer::sum);
                }

                throw new IllegalArgumentException();
            }
    ),
    NotFound(
            text -> {
                throw new IllegalArgumentException();
            }
    );


    private final CalculateFormula calculateFormula;

    TextCalculateType(CalculateFormula calculateFormula) {
        this.calculateFormula = calculateFormula;
    }
    private static class Constants {
        public static final String CUSTOM_DELIMITER_REGEX = "//(.)\n(.*)";
        public static final String COMMA_AND_COLON_REGEX = ",|:";
        private static final int EMPTY_NUMBER = 0;
        private static final int EMPTY_OR_NULL_NUMBER = 0;
    }

    public static TextCalculateType of(final String text) {
        if (text == null || text.isBlank()) {
            return NullOrEmpty;
        }

        return parseType(text);
    }

    private static TextCalculateType parseType(final String text) {
        if (isSingleNumber(text)) {
            return SingleNumber;
        }

        return parseTypeByRegex(text);
    }

    private static TextCalculateType parseTypeByRegex(final String text) {
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

    private static boolean isCommaAndColonType(final String text) {
        String[] tokens = text.split(Constants.COMMA_AND_COLON_REGEX);

        if (tokens.length == Constants.EMPTY_NUMBER) {
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
        Matcher m = Pattern.compile(Constants.CUSTOM_DELIMITER_REGEX).matcher(text);
        return m.find();
    }

    public CalculateFormula getCalculateFormula() {
        return calculateFormula;
    }


}
