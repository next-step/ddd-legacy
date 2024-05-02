package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {

    public int add(String text) {
        if (text == null || text.isEmpty()) return 0;

        String[] numbers;

        // 커스텀 구분자 지정
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            numbers = m.group(2).split(customDelimiter);
            return Arrays.stream(numbers)
                    .filter(this::isPositiveNumber)
                    .mapToInt(Integer::parseInt).sum();
        }

        numbers = text.split("[,|;]");
        return Arrays.stream(numbers)
                .filter(this::isPositiveNumber)
                .mapToInt(Integer::parseInt).sum();
    }

    private boolean isPositiveNumber(String input) {
        int number = Integer.parseInt(input);

        if (number > 0) return true;
        throw new IllegalArgumentException("입력값은 음수 일 수 없습니다.");
    }
}
