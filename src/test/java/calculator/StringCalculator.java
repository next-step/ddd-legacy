package calculator;

import calculator.delimiter.Delimiters;
import calculator.number.PositiveNumber;
import calculator.verifier.NumberVerifier;
import java.util.List;
import org.apache.logging.log4j.util.Strings;

public class StringCalculator {

    private static final int ZERO = 0;

    private StringCalculator() {
    }

    public static int calculate(final String expression, Delimiters delimiters) {
        if (Strings.isBlank(expression)) {
            return ZERO;
        }

        List<String> splitExpressions = delimiters.split(expression);

        return splitExpressions.stream()
                               .map(PositiveNumber::new)
                               .map(PositiveNumber::value)
                               .reduce(0, Integer::sum);
    }

}
