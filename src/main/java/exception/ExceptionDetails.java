package exception;

public enum ExceptionDetails {

    INVALID_NUMBER_FORMAT_EXCEPTION("숫자만 입력해주세요.")
    , NOT_POSITIVE_NUMBER_EXCEPTION("양의 숫자만 입력해주세요.")
    ;

    private final String message;

    ExceptionDetails(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}
