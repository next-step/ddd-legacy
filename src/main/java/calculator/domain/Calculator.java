package calculator.domain;

public sealed abstract class Calculator permits StringCalculator {
    protected final InputParser parser;

    protected Calculator(InputParser parser) {
        this.parser = parser;
    }
}
