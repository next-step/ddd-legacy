package calculator.exception;

public enum ErrorCode implements ErrorType {
    NEGATIVE_NOT_ALLOWED("500", "문자열 계산기에서 음수는 허용되지 않습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", code, message);
    }
}
