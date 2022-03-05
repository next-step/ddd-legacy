package stringcalculator.operator;

import stringcalculator.operator.pattern.CustomOperatorPattern;
import stringcalculator.operator.pattern.DefaultOperatorPattern;
import stringcalculator.operator.pattern.OperatorPattern;

import java.util.ArrayList;
import java.util.function.Supplier;

public class SelectedOperator {

    private final Operator operator;

    private SelectedOperator(Operator operator) {
        this.operator = operator;
    }

    public SelectedOperator(String input) {
        this(
                operatorPatterns().stream()
                        .filter(pattern -> pattern.isPattern(input))
                        .map(pattern -> pattern.getOperator(input))
                        .findFirst()
                        .orElseThrow(error(input))
        );
    }

    private static ArrayList<OperatorPattern> operatorPatterns() {
        return new ArrayList<OperatorPattern>() {{
            add(new DefaultOperatorPattern());
            add(new CustomOperatorPattern());
        }};
    }

    private static Supplier<IllegalArgumentException> error(String input) {
        return () -> new IllegalArgumentException(new OperatorNotFoundMessage(input).toString());
    }

    public int add() {
        return operator.add();
    }

    protected Operator getOperator() {
        return operator;
    }

}
