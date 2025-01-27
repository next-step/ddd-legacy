package calculator.exception;

public class NegativeNumberException extends RuntimeException {
    public NegativeNumberException(final String message) {
        super(message);
    }
}
