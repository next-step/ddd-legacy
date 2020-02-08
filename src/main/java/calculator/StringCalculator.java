package calculator;

import java.util.List;

public class StringCalculator {

    private final StringParser stringParser;

    public StringCalculator() {
        this.stringParser = new StringParser();
    }

    public int add(String text) {
        List<PositiveNumber> numbers = stringParser.split(text);
        return sum(numbers);
    }

    private int sum(List<PositiveNumber> numbers) {
        return numbers.stream().map(PositiveNumber::getPrimitiveNumber).reduce(0, Integer::sum);
    }
}
