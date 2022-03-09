package calculator.common.exception;

public class InvalidArgumentException extends RuntimeException {
    private static final long serialVersionUID = -4794430324317397809L;

    public InvalidArgumentException(String message) {
        super(message);
    }

}
