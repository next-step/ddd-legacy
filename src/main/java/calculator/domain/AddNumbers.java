package calculator.domain;

import java.util.List;
import java.util.stream.Collectors;

public class AddNumbers {

    private final List<AddNumber> values;

    public AddNumbers(List<AddNumber> values) {
        this.values = values;
    }

    public static AddNumbers from(List<String> splitText) {
        return new AddNumbers(
            splitText.stream()
                .map(AddNumber::from)
                .collect(Collectors.toList())
        );
    }

    public AddNumber addAllNumbers() {
        int sum = values.stream()
            .mapToInt(AddNumber::getValue)
            .sum();

        return new AddNumber(sum);
    }
}
