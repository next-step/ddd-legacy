package kitchenpos.stringcalculator;

import java.util.List;

public class StringCalculator {

    public int add(final String text) {
        final List<ParsedNumber> parsedNumbers = NumberParser.parse(text);
        return calculate(OperationType.ADD, parsedNumbers);
    }

    private int calculate(final OperationType operationType, final List<ParsedNumber> parsedNumbers) {
        return operationType.getOperation().operate(parsedNumbers);
    }

}
