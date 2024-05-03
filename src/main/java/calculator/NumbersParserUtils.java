package calculator;

import calculator.exception.IllegalDelimiterArgumentException;
import calculator.strategy.BasicSplitStrategy;
import calculator.strategy.CustomSplitStrategy;
import calculator.strategy.NumbersSplitStrategy;
import calculator.strategy.ZeroNumbersSplitStrategy;

import java.util.List;

public class NumbersParserUtils {
    static final String INVALID_DELIMITER_MESSAGE = "잘못된 구분자가 입력 되었습니다.";

    private static final List<NumbersSplitStrategy> STRATEGIES =
            List.of(new ZeroNumbersSplitStrategy(), new BasicSplitStrategy(), new CustomSplitStrategy());

    private NumbersParserUtils() {
        throw new AssertionError("This class should not be instantiated.");
    }

    public static Numbers parse(String input) {
        return STRATEGIES.stream()
                .filter(strategy -> strategy.isMatchPattern(input))
                .findAny()
                .map(strategy -> strategy.extract(input))
                .orElseThrow(() -> new IllegalDelimiterArgumentException(INVALID_DELIMITER_MESSAGE, input));
    }
}
