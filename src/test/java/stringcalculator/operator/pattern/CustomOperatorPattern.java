package stringcalculator.operator.pattern;

import stringcalculator.operator.CustomOperator;
import stringcalculator.operator.Operator;

import java.util.regex.Pattern;

public class CustomOperatorPattern implements OperatorPattern {

    private final Pattern pattern;

    public CustomOperatorPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public CustomOperatorPattern() {
        this(Pattern.compile("//(.)\\\\n(\\d+\\1)*\\d+"));
    }

    @Override
    public boolean isPattern(String input) {
        return pattern.matcher(input).matches();
    }

    @Override
    public Operator getOperator(String input) {
        return CustomOperator.from(input);
    }

}
