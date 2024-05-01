package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator implements Calculator<Integer, String> {

    @Override
    public Integer calculate(String input) {
        if (input == null || "".equals(input)) {
            return 0;
        }
        Matcher matcher = Pattern.compile("//(.)\n(.*)").matcher(input);
        String delimiter = ",|:";
        if (matcher.find()) {
            delimiter += "|" + matcher.group(1);
            input = matcher.group(2);
        }
        String[] parts = input.split(delimiter);
        boolean isValidPattern = Arrays.stream(parts)
                .flatMapToInt(String::chars)
                .allMatch(Character::isDigit);
        if (!isValidPattern) {
            throw new RuntimeException("숫자 이외의 값은 허용하지 않습니다.");
        }
        return Arrays.stream(parts)
                .mapToInt(Integer::parseInt)
                .sum();
    }
}
