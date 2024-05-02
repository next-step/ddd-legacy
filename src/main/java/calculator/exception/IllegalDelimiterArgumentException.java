package calculator.exception;

public class IllegalDelimiterArgumentException extends IllegalArgumentException {
    public IllegalDelimiterArgumentException(String message, String value) {
        super(String.format(message.concat(" : Invalid input = %s"), value));
    }
}
