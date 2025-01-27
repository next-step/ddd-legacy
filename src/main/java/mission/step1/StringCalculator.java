package mission.step1;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public static final String DELIMITER = "[,:]";
    public static final int ZERO_VALUE = 0;
    public static final String EMPTY_EXPRESSION = "";
    private static final String CUSTOM_DELIMITER_PATTERN = "//(.*)\n(.*)";

    public String[] splitWithDelimiter(String expression) {
        return expression.split(DELIMITER);
    }

    public int toInt(String expression) {
        try {
            int number = Integer.parseInt(expression);
            validatePositiveNumber(number);
            return number;
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자 형식이 아닌 값이 포함되어 있습니다: " + expression);
        }
    }

    private void validatePositiveNumber(int number) {
        if (number < 0) {
            throw new RuntimeException("음수는 허용되지 않습니다: " + number);
        }
    }

    public int hasText(String expression) {
        if (expression.equals(EMPTY_EXPRESSION)) {
            return ZERO_VALUE;
        }

        return toInt(expression);
    }

    public int add(String expression) {
        int result = 0;
        for (String string : splitWithDelimiter(expression)) {
            result += hasText(string);
        }
        return result;
    }

    public String[] splitWithCustomDelimiter(String expression) {
        Matcher matcher = Pattern.compile(CUSTOM_DELIMITER_PATTERN).matcher(expression);

        if (!matcher.matches()) {
            throw new RuntimeException("커스텀 구분자 형식이 올바르지 않습니다");
        }

        String delimiter = matcher.group(1);
        validateCustomDelimiterLength(delimiter, 2);

        return matcher.group(2).split(Pattern.quote(delimiter));
    }

    public void validateCustomDelimiterLength(String expression, int length) {
        if (expression.length() >= length) {
            throw new RuntimeException("커스텀 구분자의 길이는 " + length + "를 넘을 수 없습니다");
        }
    }
}
