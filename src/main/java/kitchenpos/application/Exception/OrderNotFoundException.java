package kitchenpos.application.Exception;


public class OrderNotFoundException extends EntityNotFoundException {

    public OrderNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public OrderNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public OrderNotFoundException(){
        super(ErrorCode.ORDER_NOT_FOUND);
    }
}
