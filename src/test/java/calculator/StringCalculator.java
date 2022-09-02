package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static kitchenpos.utils.NumberUtils.sum;
import static kitchenpos.utils.NumberUtils.validNativeNumber;

public class StringCalculator {

    private static final Pattern regex = Pattern.compile("//(.)\n(.*)");

    private final String DELIMITER = ",:";

    public int add(String text) {

        if (text == null || text.isBlank()) {
            return 0;
        }

        String originText = findOriginText(text);
        String delimiters = createDelimiters(text);

        String[] extractNumbers = originText.split(delimiters);

        validNativeNumber(extractNumbers);
        return sum(extractNumbers);
    }

    private String createDelimiters(String text) {
        String customDelimiter = findCustomDelimiter(text);
        if (customDelimiter != null) {
            return "[" + DELIMITER + customDelimiter + "]";
        }
        return "[" + DELIMITER + "]";
    }

    private String findOriginText(String text) {
        Matcher m = regex.matcher(text);
        return m.find() ? m.group(2) : text;
    }

    private String findCustomDelimiter(String text) {
        Matcher m = regex.matcher(text);
        return m.find() ? m.group(1) : null;
    }

}
