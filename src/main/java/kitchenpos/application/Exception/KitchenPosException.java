package kitchenpos.application.Exception;

// 모든 애플리케이션 예외의 기본 클래스
public abstract class KitchenPosException extends RuntimeException {
    private final ErrorCode errorCode;

    protected KitchenPosException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected KitchenPosException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }


    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
