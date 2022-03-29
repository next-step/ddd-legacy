package kitchenpos.domain.exception;

public class OrderLineItemNotMatchException extends IllegalStateException {

    public OrderLineItemNotMatchException() {
        super("등록되지 않은 메뉴는 주문할 수 없습니다.");
    }
}
