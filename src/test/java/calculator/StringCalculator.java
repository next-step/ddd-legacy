package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.stream;

public class StringCalculator {

    private static final Pattern regex = Pattern.compile("//(.)\n(.*)");

    private final String DELIMITER = ",:";

    public int add(String text) {

        if (text == null || text.isBlank()) {
            return 0;
        }

        String originText = findOriginText(text);
        String delimiters = createDelimiters(text);

        return sum(originText.split(delimiters));
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

    private static int sum(String[] tokens) {
        validNativeNumber(tokens);
        return stream(tokens).mapToInt(Integer::parseInt).sum();
    }

    private static void validNativeNumber(String[] tokens) {
        for (String token : tokens) {
            if (Integer.parseInt(token) < 0) {
                throw new IllegalArgumentException();
            }
        }
    }

}
