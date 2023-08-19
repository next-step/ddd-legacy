package calculator.exception;

public class NegativeInputException extends RuntimeException {
    private static final String MESSAGE = "음수는 입력받을 수 없습니다.";

    public NegativeInputException() {
        super(MESSAGE);
    }
}
