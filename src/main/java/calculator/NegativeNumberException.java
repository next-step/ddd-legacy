package calculator;

public class NegativeNumberException extends RuntimeException {

    public NegativeNumberException() {
        super("음수가 될 수 없습니다.");
    }
}
