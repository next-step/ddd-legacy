package calculator;

import org.springframework.util.StringUtils;

import java.util.Arrays;

public class StringCalculator {
    private static final Number ZERO = new Number("0");
    private StringParser stringParser = new StringParser();

    public int add(String inputText) {
        if (StringUtils.isEmpty(inputText)) {
            return 0;
        }

        return Arrays.stream(stringParser.parse(inputText))
                .map(Number::new)
                .reduce(ZERO, Number::sum).getValue();
    }
}
