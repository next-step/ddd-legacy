package calculator;

import java.util.List;

public class StringCalculator {

    private final NumberValidator validator;
    private final StringParser stringParser;

    public StringCalculator() {
        this.validator = new NumberValidator();
        this.stringParser = new StringParser();
    }

    public int add(String text) {
        List<Integer> numbers = stringParser.split(text);
        numbers.forEach(integer -> validator.validate(integer));
        return sum(numbers);
    }

    private int sum(List<Integer> numbers) {
        return numbers.stream().reduce(0, Integer::sum);
    }
}
