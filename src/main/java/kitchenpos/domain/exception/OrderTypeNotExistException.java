package kitchenpos.domain.exception;

public class OrderTypeNotExistException extends IllegalStateException {

    public OrderTypeNotExistException() {
        super("주문 유형이 올바르지 않습니다");
    }
}
