package calculator;

import java.util.Arrays;
import java.util.List;

public class Tokens {

    public static final String DEFAULT_DELIMITER = ",|:";
    public static final String PREFIX_OF_CUSTOM_DELIMITER = "//";
    public static final String SUFFIX_OF_CUSTOM_DELIMITER = "\n";

    private final String expression;

    public Tokens(String expression) {
        this.expression = expression;
    }

    public int sum() {
        String[] strNumbers = extractStrNumbers();
        List<Token> tokens = Arrays.stream(strNumbers)
                .map(Token::new)
                .toList();
        return tokens.stream()
            .mapToInt(Token::value)
            .reduce(0, Integer::sum);
    }

    private String[] extractStrNumbers() {
        String delimiter = extractDelimiter();
        String expressionRemovedDelimiter = expressionRemovedDelimiter();
        return expressionRemovedDelimiter.split(delimiter);
    }

    private String extractDelimiter() {
        if (!expression.startsWith(PREFIX_OF_CUSTOM_DELIMITER)) {
            return DEFAULT_DELIMITER;
        }
        int endIndex = expression.indexOf(SUFFIX_OF_CUSTOM_DELIMITER);
        return expression.substring(PREFIX_OF_CUSTOM_DELIMITER.length(), endIndex);
    }

    private String expressionRemovedDelimiter() {
        if (!expression.startsWith(PREFIX_OF_CUSTOM_DELIMITER)) {
            return expression;
        }
        return expression.substring(expression.indexOf(SUFFIX_OF_CUSTOM_DELIMITER) + 1);
    }
}
