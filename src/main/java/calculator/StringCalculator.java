package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.util.Strings;

public class StringCalculator {

    private static final Pattern DEFAULT_TEXT_PATTERN = Pattern.compile("[,;]");
    private static final Pattern CUSTOMIZED_TEXT_PATTERN = Pattern.compile("//(.)\n(.*)");

    public static int calculate(String text) {
        int sum = 0;

        if (Strings.isEmpty(text)) {
            return sum;
        }

        Matcher matcher = CUSTOMIZED_TEXT_PATTERN.matcher(text);
        if (matcher.find()) {
            String customDelimiter = matcher.group(1);
            String[] tokens = matcher.group(2).split(customDelimiter);
            sum = getSum(sum, tokens);
            return sum;
        }

        String[] tokens = DEFAULT_TEXT_PATTERN.split(text);
        sum = getSum(sum, tokens);
        return sum;
    }

    private static int getSum(int sum, String[] tokens) {
        for (String token : tokens) {
            int value = Integer.parseInt(token);
            if (value < 0) {
                throw new RuntimeException("음수");
            }
            sum += value;
        }
        return sum;
    }
}
