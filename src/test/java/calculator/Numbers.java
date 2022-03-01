package calculator;

import java.util.Arrays;
import java.util.List;
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

    private final List<Integer> numbers;

    private Numbers(List<Integer> numbers) {
        this.numbers = numbers;
    }

    public static Numbers of(String[] split) {
        return new Numbers(convertCollection(split));
    }

    private static List<Integer> convertCollection(String[] split) {
        return Arrays.asList(split)
                .stream()
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    public int sum() {
        return this.numbers.stream()
                .mapToInt(i -> i)
                .sum();
    }
}
