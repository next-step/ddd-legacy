package stringcalculator;

import stringcalculator.calculator.Calculator;
import stringcalculator.calculator.IntegerAdder;
import stringcalculator.validator.NonNegativeIntegerValidator;
import stringcalculator.validator.Validator;

public enum CalculateType {
    NON_NEGATIVE_INTEGER_ADDER(IntegerAdder.getInstance(), NonNegativeIntegerValidator.getInstance()),
    ;

    private final Calculator calculator;
    private final Validator validator;

    CalculateType(Calculator calculator, Validator validator) {
        this.calculator = calculator;
        this.validator = validator;
    }

    int getResult(ParsedNumbers parsedNumbers) {
        this.validator.execute(parsedNumbers);
        return this.calculator.execute(parsedNumbers);
    }

}
