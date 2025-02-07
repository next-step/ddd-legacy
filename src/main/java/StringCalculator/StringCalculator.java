package StringCalculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculator {
    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        String delimiter = DEFAULT_DELIMITER;
        String numbersStr = text;

        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (matcher.matches()) {
            delimiter = Pattern.quote(matcher.group(1));
            numbersStr = matcher.group(2);
        }

        List<Number> numberList = Arrays.stream(numbersStr.split(delimiter))
                .map(Number::new)
                .collect(Collectors.toList());

        return new Numbers(numberList).sum();
    }
}
