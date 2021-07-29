package kitchenpos.stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TextCalculateTypes {
    NullOrEmpty(text -> 0),
    SingleNumber(
            text -> {
                ParsedNumber parsedNumber = new ParsedNumber(text);
                return parsedNumber.getNumber();
            }
    ),
    CommaAndColon(
            text -> Arrays.stream(text.split(",|:"))
                    .map(ParsedNumber::new)
                    .map(ParsedNumber::getNumber)
                    .reduce(0, Integer::sum)
    ),
    CustomDelimiter(
            text -> {
                Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
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

    public CalculateFormula getCalculateFormula() {
        return calculateFormula;
    }

    TextCalculateTypes(CalculateFormula calculateFormula) {
        this.calculateFormula = calculateFormula;
    }

    public static TextCalculateTypes of(final String text) {
        if (text == null || text.isBlank()) {
            return NullOrEmpty;
        }
        if (isSingleNumber(text)) {
            return SingleNumber;
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
        return m.find();
    }
}
