package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formula {
    private String formula;
    private final Operator operator;
    private final Operand operand;

    public Formula(String formula) {
        this.formula = formula;
        this.operator = new Operator(",", ":");
        this.operand = new Operand();
    }

    public int calculate() {
        checkAndAddCustomOperator();
        this.operand.set(this.formula);
        return this.operand.operation(this.operator.getOperators());
    }

    private void checkAndAddCustomOperator() {
        Matcher matcher = Pattern.compile("//(.)\n(.*)").matcher(this.formula);
        if (matcher.find()) {
            this.operator.addCustomOperator(matcher.group(1));
            this.formula = matcher.group(2);
        }
    }
}
