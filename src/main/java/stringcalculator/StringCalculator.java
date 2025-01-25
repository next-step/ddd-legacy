package stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public class StringCalculator {

    private static final String DEFAULT_DELIMITER = ",|:";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

    public int add(String text) {
        if (isEmpty(text)) {
            return 0;
        }
        List<ZeroOrPositiveNumber> numbers = convertToNumbers(text);
        return sum(numbers);
    }

    private boolean isEmpty(String text) {
        return text == null || text.isBlank();
    }

    private List<ZeroOrPositiveNumber> convertToNumbers(String text) {
        String[] textArray = split(text);

        return Arrays.stream(textArray)
                .map(ZeroOrPositiveNumber::new)
                .collect(toList());
    }

    private String[] split(String text) {
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (matcher.find()) {
            String customDelimiter = matcher.group(1);
            return matcher.group(2).split(customDelimiter);
        }
        return text.split(DEFAULT_DELIMITER);
    }

    private int sum(List<ZeroOrPositiveNumber> numbers) {
        ZeroOrPositiveNumbers numberList = new ZeroOrPositiveNumbers(numbers);
        return numberList.sum().getNumber();
    }
}
