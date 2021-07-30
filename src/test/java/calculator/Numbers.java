package calculator;

import java.util.List;
import java.util.stream.Collectors;

public class Numbers {
    private final List<Number> numbers;

    public Numbers(String text) {
        numbers = TextParser.parseToExtractNumbers(text)
            .stream()
            .collect(Collectors.toUnmodifiableList());
    }

    public Integer sum() {
        return numbers.stream().mapToInt(Number::getNumber).sum();
    }
}
