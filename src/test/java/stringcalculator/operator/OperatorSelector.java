package stringcalculator.operator;

import java.util.Arrays;
import java.util.function.Function;
import java.util.regex.Pattern;

public enum OperatorSelector {

    CUSTOM_DELIMITER(Pattern.compile("//(.)\\\\n(\\d+\\1)*\\d+"), CustomDelimiterOperator::new),
    DEFAULT_DELIMITER(Pattern.compile("(\\d+[,:])+\\d+"), DefaultDelimiterOperator::new);

    private static final String OPERATOR_NOT_FOUND_MESSAGE = "계산 할 수 없는 형태의 입력값입니다. : %s";

    private final Pattern matchPattern;
    private final Function<String, Operator> operatorFunction;

    OperatorSelector(Pattern matchPatterns, Function<String, Operator> operatorFunction) {
        this.matchPattern = matchPatterns;
        this.operatorFunction = operatorFunction;
    }

    public static Operator selectOperator(String input) {
        return Arrays.stream(values())
                .filter(operatorSelector -> operatorSelector.matchPattern
                                            .matcher(input)
                                            .matches())
                .map(operatorSelector -> operatorSelector.operatorFunction.apply(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(OPERATOR_NOT_FOUND_MESSAGE, input)));
    }
}
