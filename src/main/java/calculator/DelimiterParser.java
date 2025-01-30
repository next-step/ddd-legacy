package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DelimiterParser {

    private static final String DEFAULT_DELIMITER = "[,:\\n]";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

    public static String[] parse(String input) {
        String delimiter = DEFAULT_DELIMITER;
        String numbersToCalculate = input;

        Matcher m = CUSTOM_DELIMITER_PATTERN.matcher(input);
        if (m.find()) {
            delimiter = Pattern.quote(m.group(1)); // 구분자를 이스케이프 처리
            numbersToCalculate = m.group(2);
        }

        return numbersToCalculate.split(delimiter);
    }
}
