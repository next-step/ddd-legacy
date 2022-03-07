package StringAddCalculator;

public class NegativeNumberException extends RuntimeException{

    private static final String MESSAGE = "음수는 처리할 수 없습니다.";

    public NegativeNumberException() {
        super(MESSAGE);
    }
}
