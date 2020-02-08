package calculator;

import org.springframework.util.StringUtils;

import java.util.Arrays;

public class StringCalculator {
    private StringParser stringParser = new StringParser();

    public int add(String inputText) {
        if (StringUtils.isEmpty(inputText)) {
            return 0;
        }

        return Arrays.stream(stringParser.parse(inputText))
                .map(Number::new)
                .mapToInt(Number::getValue)
                .sum();
    }
}
