package calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Numbers {

    private List<Integer> numbers = new ArrayList<>();

    public Numbers(String[] values) {
        this.numbers = toList(values);
    }

    public int sum() {
        return numbers.stream().mapToInt(it -> it).sum();
    }

    private static List<Integer> toList(String[] values) {
        return Arrays.stream(values)
                .map(Numbers::toInt)
                .toList();
    }

    private static int toInt(String values) {
        int value = Integer.parseInt(values);
        if (value < 0) {
            throw new RuntimeException("value should not be negative");
        }
        return value;
    }
}
