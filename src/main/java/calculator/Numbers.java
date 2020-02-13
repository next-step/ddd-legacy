package calculator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Geonguk Han
 * @since 2020-02-08
 */
public class Numbers {

    private final List<Number> numbers;

    public Numbers(final List<Number> numbers) {
        this.numbers = new ArrayList<>(numbers);
    }

    public int sum() {
        return numbers.stream()
                .reduce(new Number(0), Number::sum)
                .getValue();
    }
}
