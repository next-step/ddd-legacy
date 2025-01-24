package calculator.domain;

public sealed abstract class Calculator permits StringCalculator {
    protected final Validator validator;
    protected final StringCalculatorInputParser parser;

    protected Calculator(Validator validator, StringCalculatorInputParser parser) {
        this.validator = validator;
        this.parser = parser;
    }
}
