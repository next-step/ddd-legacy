package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {

    public static final String SEPARATOR = "[,:]";

    public int calculate(String input) {
        try {
            if (input == null || input.isEmpty()) {
                return 0;
            }

            Matcher m = Pattern.compile("//(.)\\\\n(.*)").matcher(input);

            if (m.find()) {
                String customDelimiter = m.group(1);
                String[] splitInput= m.group(2).split(customDelimiter);
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
                    return Integer.parseInt(a);
                }).sum();
    }
}
