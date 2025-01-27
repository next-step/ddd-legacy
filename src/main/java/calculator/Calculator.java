package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {

    public static final String SEPARATOR = "[,:]";
    public static final Pattern CUSTOM_SEPARATOR_PATTERN = Pattern.compile("//(.)\\\\n(.*)");

    public int calculate(String input) {
        if (isEmpty(input)) {
            return 0;
        }

        try {
            Matcher m = CUSTOM_SEPARATOR_PATTERN.matcher(input);

            if (m.find()) {
                String customDelimiter = m.group(1);
                String[] splitInput = m.group(2).split(customDelimiter);
                return sum(splitInput);
            } else {
                String[] splitInput = input.split(SEPARATOR);
                return sum(splitInput);
            }

        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자의 형태가 아닙니다.", e);
        }
    }

    private int sum(String[] splitInput) {
        return Arrays.stream(splitInput)
                .mapToInt(a -> {
                    if (a.isEmpty()) {
                        return 0;
                    }
                    int number = Integer.parseInt(a);
                    if (number < 0) {
                        throw new RuntimeException("음수는 들어올 수 없습니다.");
                    }
                    return number;
                }).sum();
    }

    private boolean isEmpty(String input) {
        return input == null || input.isEmpty();
    }
}
