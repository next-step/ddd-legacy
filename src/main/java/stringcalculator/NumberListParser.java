package stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NumberListParser {
    private static final Pattern PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final String COMMA_OR_COLON = ",|:";
    private static final int CUSTOM_DELIMITER_NO = 1;
    private static final int CUSTOM_DELIMITER_NUMBERS_NO = 2;

    public static NumberList parse(String input) {
        Matcher matcher = PATTERN.matcher(input);
        if (matcher.find()) {
            String customDelimiter = matcher.group(CUSTOM_DELIMITER_NO);
            String[] numberStringArray = matcher.group(CUSTOM_DELIMITER_NUMBERS_NO).split(customDelimiter);
            List<Number> numberList = createNumberList(numberStringArray);
            return NumberList.of(numberList);
        }
        return NumberList.of(createNumberList(input.split(COMMA_OR_COLON)));
    }

    private static List<Number> createNumberList(String[] numbers) {
        return Arrays.stream(numbers)
                .map(NumberListParser::convertNumber)
                .collect(Collectors.toList());
    }

    private static Number convertNumber(String number) {
        return Number.of(Integer.parseInt(number));
    }
}
