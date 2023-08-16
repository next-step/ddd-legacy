package calculator;

import java.util.List;

public class StringCalculator {

    public int run(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }

        List<PositiveNumber> positiveNumbers = NumberParser.parse(str);
        return positiveNumbers.stream()
                .reduce(new PositiveNumber(0), PositiveNumber::plus)
                .getValue();
    }
}
