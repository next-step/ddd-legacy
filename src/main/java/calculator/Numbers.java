package calculator;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Geonguk Han
 * @since 2020-02-08
 */
public class Numbers {

    private List<Number> numbers;

    public Numbers(List<Number> numbers) {
        this.numbers = numbers;
    }

    public int sum() {
        return numbers.stream()
                .map(number -> number.getValue())
                .reduce(0, Integer::sum);
    }
}
