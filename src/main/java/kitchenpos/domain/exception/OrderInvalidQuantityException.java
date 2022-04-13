package kitchenpos.domain.exception;

public class OrderInvalidQuantityException extends IllegalStateException {

    public OrderInvalidQuantityException(long quantity) {
        super("최소 주문 수량은 0개 이상입니다. 주문 수량 : " + quantity);
    }
}
