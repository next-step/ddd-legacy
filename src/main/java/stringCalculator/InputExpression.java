package stringCalculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record InputExpression(Delimiters delimiters, Numbers numbers) {
    private static final String CUSTOM_DELIMITER_EXPRESSION = "//";
    private static final Pattern CUSTOM_PATTERN;

    static {
        CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");
    }

    public static InputExpression parse(final String text) {
        if (emptyOrNull(text)) {
            return new InputExpression(Delimiters.ofDefaults(), Numbers.empty());
        }
        if (text.startsWith(CUSTOM_DELIMITER_EXPRESSION)) {
            return customize(text);
        }
        List<String> tokens = parseTokens(Delimiters.ofDefaults(), text);
        Numbers numbers = Numbers.from(tokens);
        return new InputExpression(Delimiters.ofDefaults(), numbers);
    }

    private static InputExpression customize(final String text) {
        Matcher m = CUSTOM_PATTERN.matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            Delimiters delimiters = Delimiters.customize(customDelimiter);
            List<String> tokens = parseTokens(delimiters, m.group(2));
            return new InputExpression(delimiters, Numbers.from(tokens));
        }
        return new InputExpression(Delimiters.ofDefaults(), Numbers.empty());
    }

    private static boolean emptyOrNull(final String text) {
        return text == null || "".equals(text);
    }

    private static List<String> parseTokens(final Delimiters delimiters, final String text) {
        String delimiterExpression = String.join("|", delimiters.list());
        return Arrays.stream(text.split(delimiterExpression)).toList();
    }
}



