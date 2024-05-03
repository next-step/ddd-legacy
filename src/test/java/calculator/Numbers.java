package calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Numbers {

    private List<Number> numbers = new ArrayList<>();

    public Numbers(String[] values) {
        this.numbers = toList(values);
    }

    private static List<Number> toList(String[] values) {
        return Arrays.stream(values)
                .map(Number::of)
                .toList();
    }

    public int sum() {
        return numbers.stream().reduce(new Number(0), Number::add).value();
    }

}
