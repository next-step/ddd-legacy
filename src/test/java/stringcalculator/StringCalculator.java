package stringcalculator;

import java.util.List;

public class StringCalculator {

    private final StringSeparation separation;

    public StringCalculator(StringSeparation separation) {
        this.separation = separation;
    }

    public int add(String text) {
        List<Number> separate = separation.separate(text);

        Number result = separate.stream()
                .reduce(Number.ZERO, Number::plus);

        return result.getValue();
    }

}
