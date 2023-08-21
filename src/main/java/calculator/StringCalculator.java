package calculator;

import calculator.domain.NumberParser;
import calculator.util.Validate;

public class StringCalculator {

    private final NumberParser numberParser = new NumberParser();

    public int add(final String input) {
        if (Validate.isNullOrBlank(input)) {
            return 0;
        }

        return numberParser.parser(input)
                .sum();
    }


}
