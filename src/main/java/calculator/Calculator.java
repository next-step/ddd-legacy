package calculator;

import org.springframework.util.StringUtils;

class Calculator {
    static final Integer DEFAULT_RESULT = 0;

    Integer calculate(String text) {
        if (StringUtils.isEmpty(text)) {
            return DEFAULT_RESULT;
        }

        return new PositiveNumbers(InputParser.parseToInts(text))
                .sum(DEFAULT_RESULT);
    }
}
