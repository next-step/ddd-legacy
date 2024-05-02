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

    private List<InputNumber> inputNumbers;

    public StringCalculator() {
        this.inputNumbers = new ArrayList<>();
    }

    public int add(String text) {
        if (Objects.isNull(text) || text.isBlank()) {
            return 0;
        }

        String[] tokens = getValue(text);
        inputNumbers = Arrays.stream(tokens)
                .map(InputNumber::new)
                .toList();

        return sum(inputNumbers);
    }

    private String[] getValue(String text) {
        Matcher m = CUSTOM_DELIMITER.matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }

        return text.split(DEFAULT_DELIMITER);
    }

    private int sum(List<InputNumber> inputNumbers) {
        return inputNumbers.stream()
                .map(InputNumber::getNumber)
                .mapToInt(Integer::parseInt)
                .sum();
    }


}
