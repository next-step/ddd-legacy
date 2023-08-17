package stringaddcalculator;

public class NullOrEmptyExpressionException extends RuntimeException {
    private static final String MESSAGE = "식은 빈 문자열 또는 null을 입력할 수 없습니다. 현재 값: ";

    public NullOrEmptyExpressionException(String extraMessage) {
        super(MESSAGE + extraMessage);
    }
}
