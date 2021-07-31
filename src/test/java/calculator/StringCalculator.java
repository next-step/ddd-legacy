package calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    public int add(final String text) {
        if (text == null) {
            return 0;
        }

        if (text.isEmpty()) {
            return 0;
        }

        final List<String> defaultDelimiters = Arrays.asList(",", ":");
        final List<String> delimiters = new ArrayList<>(defaultDelimiters);
        final Matcher matcher = Pattern.compile("//(.)\n(.*)").matcher(text);

        String targetText = text;
        if (matcher.find()) {
            delimiters.add(matcher.group(1));
            targetText = matcher.group(2);
        }

        final String delimiterRegex = String.join("|", delimiters);
        final String[] tokens = targetText.split(delimiterRegex);

        final String[] exceptTokens = Arrays.stream(tokens)
                .filter(token -> !token.matches("^[0-9]+$"))
                .toArray(String[]::new);
        if (exceptTokens.length > 0) {
            String errorMessage = "숫자 이외의 값 또는 음수는 전달할 수 없습니다: \""
                    + String.join("\", \"", exceptTokens)
                    + "\"";
            throw new RuntimeException(errorMessage);
        }

        return Arrays.stream(tokens)
                .mapToInt(Integer::parseInt)
                .sum();
    }
}
