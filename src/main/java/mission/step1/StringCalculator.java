package mission.step1;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private final ParserStrategy parser;
    private final CalculateStrategy calculator;

    public StringCalculator(ParserStrategy parser, CalculateStrategy calculator) {
        this.parser = parser;
        this.calculator = calculator;
    }

    private static final int ZERO_VALUE = 0;
    private static final String CUSTOM_DELIMITER_PATTERN = "//(.*)\n(.*)";
    private static final String PREFIX = "//";
    private static final Pattern CUSTOM_DELIMITER = Pattern.compile(CUSTOM_DELIMITER_PATTERN);

    public int add(String expression) {

        if (!StringUtils.hasText(expression)) {
            return ZERO_VALUE;
        }

        if (isCustomDelimiter(expression)) {
            for (String string : splitWithCustomDelimiter(expression)) {
                calculator.calculate(PositiveNumber.from(string));
            }
            return calculator.getResult();
        }

        for (String string : parser.splitWithDelimiter(expression)) {
            calculator.calculate(PositiveNumber.from(string));
        }

        return calculator.getResult();
    }

    private static boolean isCustomDelimiter(String expression) {
        return expression.startsWith(PREFIX);
    }

    public String[] splitWithCustomDelimiter(String expression) {
        Matcher matcher = CUSTOM_DELIMITER.matcher(expression);

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

        if (expression.isEmpty()) {
            throw new RuntimeException("커스텀 구분자의 길이는 " + 1 + "보다 작을 수 없습니다.");
        }
    }
}
