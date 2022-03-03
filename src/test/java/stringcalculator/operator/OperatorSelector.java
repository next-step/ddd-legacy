package stringcalculator.operator;

import stringcalculator.operator.pattern.CustomOperatorPattern;
import stringcalculator.operator.pattern.DefaultOperatorPattern;
import stringcalculator.operator.pattern.OperatorPattern;

import java.util.ArrayList;
import java.util.List;

public class OperatorSelector {

    private final List<OperatorPattern> patterns;

    public OperatorSelector(List<OperatorPattern> patterns) {
        this.patterns = patterns;
    }

    public OperatorSelector() {
        this(
                new ArrayList<OperatorPattern>() {{
                    add(new DefaultOperatorPattern());
                    add(new CustomOperatorPattern());
                }}
        );
    }

    public Operator select(String input) {
        return patterns.stream()
                .filter(pattern -> pattern.isPattern(input))
                .map(pattern -> pattern.getOperator(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(new OperatorNotFoundMessage(input).toString()));
    }

}
