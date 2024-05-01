package calculator;

import calculator.strategy.BasicSplitStrategy;
import calculator.strategy.CustomSplitStrategy;
import calculator.strategy.NumbersSplitStrategy;

import java.util.List;

import static calculator.Numbers.ZERO_NUMBERS;

public class NumbersParserUtils {

    private static final List<NumbersSplitStrategy> STRATEGIES =
            List.of(new BasicSplitStrategy(), new CustomSplitStrategy());

    private NumbersParserUtils() {
    }

    public static Numbers parse(String input) {
        return STRATEGIES.stream()
                .filter(strategy -> (input != null) && strategy.isMatchPattern(input))
                .findAny()
                .map(strategy -> strategy.extract(input))
                .orElse(ZERO_NUMBERS);
    }
}
