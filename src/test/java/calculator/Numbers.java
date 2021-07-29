package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Numbers {

    public static final Numbers ZERO = new Numbers(0);

    private List<Integer> numbers;

    public Numbers(Integer... number) {
        this.numbers = Arrays.asList(number);
    }

    public Numbers(String... tokens) {
        this.numbers = Arrays.stream(tokens)
                .mapToInt(Integer::parseInt)
                .boxed()
                .collect(Collectors.toList());
    }

    public int add() {
        return this.numbers.stream()
                .reduce((first, second) -> first + second)
                .get();
    }
}
