package calculator;

public class NegativeNotAllowedException extends RuntimeException {

    public NegativeNotAllowedException() {
        super("음수는 입력할 수 없습니다.");
    }
}
