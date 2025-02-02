package mission.step1;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomParser implements Parsable {

    private static final int inValidDelimiterSize = 2;
    private static final String CUSTOM_DELIMITER_PATTERN = "//(.*)\n(.*)";
    private static final Pattern CUSTOM_DELIMITER = Pattern.compile(CUSTOM_DELIMITER_PATTERN);

    public boolean isCustomFormat(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return CUSTOM_DELIMITER.matcher(input).matches();
    }

    @Override
    public String[] splitWithDelimiter(String expression) {
        Matcher matcher = CUSTOM_DELIMITER.matcher(expression);

        if (!matcher.find()) {
            throw new IllegalArgumentException("커스텀 구분자 형식이 올바르지 않습니다");
        }

        String delimiter = matcher.group(1);
        validateCustomDelimiterLength(delimiter);

        return matcher.group(2).split(Pattern.quote(delimiter));
    }

    public void validateCustomDelimiterLength(String expression) {
        if (expression.length() >= inValidDelimiterSize) {
            throw new RuntimeException("커스텀 구분자의 길이는 " + inValidDelimiterSize + "를 넘을 수 없습니다");
        }

        if (expression.isEmpty()) {
            throw new RuntimeException("커스텀 구분자의 길이는 " + 1 + "보다 작을 수 없습니다.");
        }
    }
}
