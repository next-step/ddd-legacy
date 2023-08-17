package calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PositiveNumbers {
    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final Pattern pattern = Pattern.compile("//(.)\n(.*)");

    private final List<PositiveNumber> numbers;

    public PositiveNumbers(final String text) {
        this.numbers = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return;
        }

        String[] stringNumbers = parseStringToNumbers(text);
        for (String stringNumber : stringNumbers) {
            if (isEmptyString(stringNumber)) {
                continue;
            }

            this.numbers.add(parseNumber(stringNumber));
        }
    }

    private String[] parseStringToNumbers(final String text) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(2)
                    .trim()
                    .split(matcher.group(1));
        }

        return text.split(DEFAULT_DELIMITER);
    }

    private boolean isEmptyString(String stringNumber) {
        return stringNumber.length() == 0;
    }

    private PositiveNumber parseNumber(String stringNumber) {
        return new PositiveNumber(stringNumber);
    }

    public int sum() {
        return this.numbers
                .stream()
                .mapToInt(PositiveNumber::getNumber)
                .sum();
    }

    public boolean isEmpty() {
        return this.numbers.isEmpty();
    }
}
