package calculator;

import org.springframework.util.StringUtils;

class Calculator {
    public static final Integer DEFAULT_RESULT = 0;

    Integer calculate(String text) {
        if (StringUtils.isEmpty(text)) {
            return DEFAULT_RESULT;
        }

        return InputParser.parseToInts(text).stream()
                .map(PositiveNumber::from)
                .reduce(PositiveNumber.from(DEFAULT_RESULT), PositiveNumber::sum)
                .getValue();
    }
}
