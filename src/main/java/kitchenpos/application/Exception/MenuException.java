package kitchenpos.application.Exception;


public class MenuException extends BusinessException {
    public MenuException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public MenuException(ErrorCode errorCode) {
        super(errorCode);
    }
}