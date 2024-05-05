package calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final String DEFAULT_DELIMITER = ",|:";
    private static final Pattern CUSTOM_DELIMITER = Pattern.compile("//(.)\n(.*)");

    private static final int CUSTOM_DELIMITER_GROUP = 1;
    private static final int CUSTOM_DELIMITER_VALUE_GROUP = 2;

    private List<PositiveNumber> inputNumbers;

    public StringCalculator() {
        this.inputNumbers = new ArrayList<>();
    }

    public int add(String text) {
        if (Objects.isNull(text) || text.isBlank()) {
            return 0;
        }

        String[] tokens = getValue(text);
        inputNumbers = Arrays.stream(tokens)
                .map(PositiveNumber::new)
                .toList();

        return sum(inputNumbers);
    }

    private String[] getValue(String text) {
        Matcher customDelimiterMatcher = CUSTOM_DELIMITER.matcher(text);

        if (customDelimiterMatcher.find()) {
            String customDelimiter = customDelimiterMatcher.group(CUSTOM_DELIMITER_GROUP);
            return customDelimiterMatcher.group(CUSTOM_DELIMITER_VALUE_GROUP).split(customDelimiter);
        }

        return text.split(DEFAULT_DELIMITER);
    }

    private int sum(List<PositiveNumber> inputNumbers) {
        return inputNumbers.stream()
                .map(PositiveNumber::getNumber)
                .mapToInt(Integer::parseInt)
                .sum();
    }


}
