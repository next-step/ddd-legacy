package calculator;

import java.util.List;

public class StringCalculator {

    public int run(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }

        List<Number> numbers = NumberParser.parse(str);
        return numbers.stream()
                .reduce(new Number(0), Number::plus)
                .getValue();
    }
}
