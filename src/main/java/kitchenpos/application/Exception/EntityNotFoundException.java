package kitchenpos.application.Exception;


public abstract class EntityNotFoundException extends KitchenPosException {
    protected EntityNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
