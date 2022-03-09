package calculator.common.exception;

public enum InvalidArgumentExceptionMessage {
    NEGATIVE_NUMBER_EXCEPTION("음수를 입력할 수 없습니다.");

    private final String message;

    InvalidArgumentExceptionMessage(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
