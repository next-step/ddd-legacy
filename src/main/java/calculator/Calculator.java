package calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {
    private static final Pattern CUSTOM_DELIMITER = Pattern.compile("//(.)\n(.*)");
    private static final String DEFAULT_DELIMITER = "[,|;]";
    private static final int CUSTOM_DELIMITER_GROUP = 1;
    private static final int SEPARATED_BY_CUSTOM_DELIMITER_GROUP = 2;


    private List<Number> numbers;

    public Calculator() {
        this.numbers = new ArrayList<>();
    }

    public int add(String text) {
        if (InputValidator.validation(text)) return 0;

        numbers = Arrays.stream(splitText(text))
                .map(Number::new)
                .toList();
        return this.plus(numbers);
    }

    private String[] splitText(String text) {
        Matcher m = CUSTOM_DELIMITER.matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(CUSTOM_DELIMITER_GROUP);
            String separatedByCustomDelimiter = m.group(SEPARATED_BY_CUSTOM_DELIMITER_GROUP);

            return separatedByCustomDelimiter.split(customDelimiter);
        }

        return text.split(DEFAULT_DELIMITER);
    }

    private int plus(List<Number> numbers) {
        return numbers.stream()
                .mapToInt(Number::getNumber)
                .sum();
    }
}
