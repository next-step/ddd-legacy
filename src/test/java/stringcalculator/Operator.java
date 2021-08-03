package stringcalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Operator {
    private final List<String> operators;

    public Operator(String... defaultOperator) {
        this.operators = new ArrayList<>();
        this.operators.addAll(Arrays.asList(defaultOperator));
    }

    public void addCustomOperator(String operator) {
        this.operators.add(operator);
    }

    public String getOperators() {
        StringBuilder operatorsToString = new StringBuilder();
        for (String operator : operators) {
            operatorsToString.append(operator).append("|");
        }
        return operatorsToString.substring(0, operatorsToString.length() - 1);
    }
}
