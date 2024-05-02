package calculator;

import io.micrometer.core.instrument.util.StringUtils;
import java.util.List;

public class StringCalculator {

    private final NumbersFactory numbersFactory;

    public StringCalculator() {
        this.numbersFactory = new NumbersFactory();
    }

    public int add(String input) {
        if (StringUtils.isEmpty(input)) {
            return 0;
        }
        return numbersFactory.getNumbers(input)
            .stream()
            .reduce(0, Integer::sum);
    }

}
