package kitchenpos.domain.exception;

public class OrderLineItemNotExistException extends IllegalStateException {

    public OrderLineItemNotExistException() {
        super("주문 상품이 없습니다.");
    }
}
