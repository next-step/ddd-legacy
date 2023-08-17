package calculator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringCalculator {
    private static final String DEFAULT_VALUE = "0";

    private final String text;
    private Delimiter delimiter;
    private List<Number> numbers;

    public StringCalculator(String text) {
        this.text = text;
        delimiter = new Delimiter(text);
        if (isNullValue()) {
            numbers = List.of(new Number(DEFAULT_VALUE));
            return;
        }
        numbers = Stream.of(delimiter.splittingText())
            .map(Number::new)
            .collect(Collectors.toList());
    }

    public int add() {
        return numbers.stream()
            .mapToInt(Number::getNumber)
            .sum();
    }

    private boolean isNullValue() {
        return text == null || text.isBlank();
    }

}
