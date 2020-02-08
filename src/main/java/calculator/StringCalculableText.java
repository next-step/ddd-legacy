package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculableText {

    private static final Pattern DEFAULT_DELIMITER_PATTERN = Pattern.compile("[,;]");
    private static final Pattern CUSTOMIZED_TEXT_PATTERN = Pattern.compile("//(.)\n(.*)");

    private String text;

    private StringCalculableText(String text) {
        this.text = text;
    }

    public static StringCalculableText of(String text) {
        return new StringCalculableText(text);
    }

    public boolean isNullOrEmpty() {
        return text == null || text.isEmpty();
    }

    public int getTotal() {
        PositiveNumber total;
        String[] tokens;
        Matcher matcher = CUSTOMIZED_TEXT_PATTERN.matcher(text);
        if (matcher.find()) {
            String customDelimiter = matcher.group(1);
            tokens = matcher.group(2).split(customDelimiter);
            total = getTotalPositiveNumbers(tokens);
            return total.getValue();
        }

        tokens = DEFAULT_DELIMITER_PATTERN.split(text);
        total = getTotalPositiveNumbers(tokens);
        return total.getValue();
    }

    private PositiveNumber getTotalPositiveNumbers(String[] tokens) {
        PositiveNumber total = PositiveNumber.of(0);
        for (String token : tokens) {
            total.sum(PositiveNumber.of(token));
        }
        return total;
    }
}
