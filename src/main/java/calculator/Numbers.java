package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Numbers {
    private final List<Number> numbers;

    private Numbers(final List<Number> numbers) {
        this.numbers = numbers;
    }

    public static Numbers of(final String text) {
        String delimiter = ",|\\:";
        final String[] splitText = text.split("\n");
        if (splitText.length > 1) {
            delimiter += String.format("|\\%s", splitText[0].charAt(2));
        }
        final String numberText = splitText.length > 1
                ? splitText[1]
                : splitText[0];

        return new Numbers(Arrays.stream(numberText.split(delimiter))
                .map(Number::of)
                .collect(Collectors.toList()));
    }

    public List<Integer> toIntList() {
        return numbers.stream()
                .map(Number::toInteger)
                .collect(Collectors.toList());
    }
}
