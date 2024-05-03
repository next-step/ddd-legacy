package calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Numbers {

    private List<Number> numbers = new ArrayList<>();

    public Numbers(String[] values) {
        this.numbers = toList(values);
    }

    public int sum() {
        return numbers.stream().mapToInt(Number::value).sum();
    }

    private static List<Number> toList(String[] values) {
        return Arrays.stream(values)
                .map(Number::of)
                .toList();
    }

}
