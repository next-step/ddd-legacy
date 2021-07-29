package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Numbers {

    public static final int ZERO = 0;

    private List<Integer> numbers;

    public Numbers(String... tokens) {
        this.numbers = Arrays.stream(tokens)
                .mapToInt(Integer::parseInt)
                .boxed()
                .collect(Collectors.toList());
    }

    public int add() {
        return this.numbers.stream()
                .reduce((first, second) -> first + second)
                .orElse(ZERO);
    }
}
