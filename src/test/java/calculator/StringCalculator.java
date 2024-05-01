package calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator implements Calculator<Integer, String> {
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

    private final List<String> delimiters = new ArrayList<>(List.of(",", ":"));

    public StringCalculator() {
    }

    @Override
    public Integer calculate(String input) {
        if (input == null || "".equals(input)) {
            return 0;
        }
        input = addCustomDelimiter(input);
        String[] parts = split(input);
        validate(parts);
        return Arrays.stream(parts)
                .mapToInt(Integer::parseInt)
                .sum();
    }

    private String addCustomDelimiter(String input) {
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(input);
        if (matcher.find()) {
            delimiters.add(matcher.group(1));
            return matcher.group(2);
        }
        return input;
    }

    private String[] split(String input) {
        return input.split(String.join("|", delimiters));
    }

    private void validate(String[] parts) {
        boolean isValidPattern = Arrays.stream(parts)
                .flatMapToInt(String::chars)
                .allMatch(Character::isDigit);
        if (!isValidPattern) {
            throw new RuntimeException("숫자 이외의 값은 허용하지 않습니다.");
        }
    }
}
