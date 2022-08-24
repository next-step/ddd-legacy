package calculator;

import calculator.delimiter.Delimiters;
import calculator.verifier.NumberVerifier;
import java.util.List;
import org.apache.logging.log4j.util.Strings;

public class StringCalculator {

    private static final int ZERO = 0;

    private StringCalculator() {
    }

    public static int calculate(final String expression, Delimiters delimiters, NumberVerifier numberVerifier) {
        if (Strings.isBlank(expression)) {
            return ZERO;
        }

        List<String> splitExpressions = delimiters.split(expression);

        numberVerifier.verify(splitExpressions);

        return splitExpressions.stream()
                               .map(splitExpression -> Integer.parseInt(splitExpression))
                               .reduce(0, Integer::sum);
    }

}
