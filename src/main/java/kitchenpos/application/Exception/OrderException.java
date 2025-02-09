package kitchenpos.application.Exception;


public class OrderException extends BusinessException {

    public OrderException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public OrderException(ErrorCode errorCode) {
        super(errorCode);
    }
}
