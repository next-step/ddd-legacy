package kitchenpos.stringcalculator;

import java.util.Arrays;

public enum OperationType {
    ADD(parsedNumbers -> {
        return parsedNumbers.stream()
                .map(ParsedNumber::getNumber)
                .reduce(0, Integer::sum);
        }
    ),
    MINUS(parsedNumbers -> {
        throw new UnsupportedOperationException("Not implemented for MINUS Operation");
        }
    ),
    ;

    private final Operation operation;

    OperationType(Operation operation) {
        this.operation = operation;
    }

    public Operation getOperation() {
        return operation;
    }
}
