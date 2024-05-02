package calculator.exception;

public enum ErrorMessage {

    IS_NOT_NEGATIVE("음수는 허용되지 않습니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}
