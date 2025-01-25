package calculator.exception;

public class NegativeNumberException extends IllegalArgumentException {
    public NegativeNumberException(String message) {
        super(message);
    }
}
