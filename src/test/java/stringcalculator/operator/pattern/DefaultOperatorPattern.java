package stringcalculator.operator.pattern;

import stringcalculator.operator.DefaultOperator;
import stringcalculator.operator.Operator;

import java.util.regex.Pattern;

public class DefaultOperatorPattern implements OperatorPattern {

    private final Pattern pattern;

    public DefaultOperatorPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public DefaultOperatorPattern() {
        this(Pattern.compile("(\\d+[,:])+\\d+"));
    }

    @Override
    public boolean isPattern(String input) {
        return pattern.matcher(input).matches();
    }

    @Override
    public Operator getOperator(String input) {
        return DefaultOperator.from(input);
    }

}
