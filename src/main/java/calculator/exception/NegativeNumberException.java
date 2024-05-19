package calculator.exception;

public class NegativeNumberException extends IllegalArgumentException {
    public static final String NEGATIVE_NUMBER_EXCEPTION = "음수는 유효하지 않은 숫자입니다.";

    public NegativeNumberException(int value) {
        super(String.format(NEGATIVE_NUMBER_EXCEPTION.concat(" : Invalid number = %s"), value));
    }
}
