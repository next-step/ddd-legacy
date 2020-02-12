package calculator;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringCalculator {
    private int result = 0;

    public StringCalculator() {
    }

    public Integer add(String text) {

        if (isEmptyOrNull(text)) {
            return result;
        }

        return new PositiveIntegers(parseToInteger(StringSplitter.extractNumberStringByDelimiter(text)))
                .sum();
    }

    private boolean isEmptyOrNull(String text) {
        return StringUtils.isEmpty(text);
    }

    private List<PositiveInteger> parseToInteger(String[] stringNumbers) {

        return Arrays.stream(stringNumbers)
                .map(strings -> getPositiveNumber(strings))
                .collect(Collectors.toList());
    }

    private PositiveInteger getPositiveNumber(String stringNumber) {
        Integer number = 0;
        try {
            number = Integer.parseInt(stringNumber);
        } catch (RuntimeException e) {
            throw new RuntimeException();
        }
        return new PositiveInteger(number);
    }
}
