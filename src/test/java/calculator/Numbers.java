package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <pre>
 * calculator
 *      Number
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-02 오전 1:54
 */

public class Numbers {

    private final List<Number> numbers;

    private Numbers(List<Number> numbers) {
        this.numbers = numbers;
    }

    public static Numbers of(String[] split) {

        return new Numbers(convertCollection(split));
    }

    private static List<Number> convertCollection(String[] split) {

        return Arrays.asList(split)
                .stream()
                .map(Number::new)
                .collect(Collectors.toList());
    }

    public int sum() {
        Number sum = this.numbers.stream()
                .reduce(Number.ZERO, (total, number) -> total.plus(number));
        return sum.value();
    }
}
