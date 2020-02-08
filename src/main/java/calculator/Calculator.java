package calculator;

import org.springframework.util.StringUtils;

class Calculator {
    Integer calculate(String text) {
        if (StringUtils.isEmpty(text)) {
            return 0;
        }

        return InputParser.parseToInts(text).stream()
                .map(PositiveNumber::from)
                .reduce(PositiveNumber.from(0), PositiveNumber::sum)
                .getValue();
    }
}
