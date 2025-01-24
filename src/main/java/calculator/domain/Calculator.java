package calculator.domain;

public sealed abstract class Calculator permits StringCalculator {
    protected final StringCalculatorInputValidator stringCalculatorInputValidator;
    protected final InputParser parser;

    protected Calculator(StringCalculatorInputValidator stringCalculatorInputValidator, InputParser parser) {
        this.stringCalculatorInputValidator = stringCalculatorInputValidator;
        this.parser = parser;
    }
}
