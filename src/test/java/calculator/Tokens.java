package calculator;

import java.util.Arrays;
import java.util.List;

public class Tokens {

    public static final String DEFAULT_DELIMITER = ",|:";
    public static final String PREFIX_OF_CUSTOM_DELIMITER = "//";
    public static final String SUFFIX_OF_CUSTOM_DELIMITER = "\n";

    private final List<Token> tokens;

    public Tokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public static Tokens from(String expression) {
        String[] strNumbers = extractStrNumbers(expression);
        List<Token> tokens = Arrays.stream(strNumbers)
            .map(Token::new)
            .toList();
        return new Tokens(tokens);
    }

    private static String[] extractStrNumbers(String expression) {
        String delimiter = extractDelimiter(expression);
        String expressionRemovedDelimiter = expressionRemovedDelimiter(expression);
        return expressionRemovedDelimiter.split(delimiter);
    }

    private static String extractDelimiter(String expression) {
        if (!expression.startsWith(PREFIX_OF_CUSTOM_DELIMITER)) {
            return DEFAULT_DELIMITER;
        }
        int endIndex = expression.indexOf(SUFFIX_OF_CUSTOM_DELIMITER);
        return expression.substring(PREFIX_OF_CUSTOM_DELIMITER.length(), endIndex);
    }

    private static String expressionRemovedDelimiter(String expression) {
        if (!expression.startsWith(PREFIX_OF_CUSTOM_DELIMITER)) {
            return expression;
        }
        return expression.substring(expression.indexOf(SUFFIX_OF_CUSTOM_DELIMITER) + 1);
    }

    public int sum() {
        return tokens.stream()
            .mapToInt(Token::value)
            .reduce(0, Integer::sum);
    }
}
