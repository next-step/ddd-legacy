package calculator.exception;

public class IllegalNumberException extends IllegalArgumentException {
    public IllegalNumberException(String message, String value) {
        super(String.format(message.concat(" : Invalid number = %s"), value));
    }
}
