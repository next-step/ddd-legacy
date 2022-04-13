package kitchenpos.domain.exception;

public class OrderFromEmptyOrderTableException extends IllegalStateException {

    private static final String DEFAULT_MESSAGE = "착석 후 주문 가능합니다.";

    public OrderFromEmptyOrderTableException() {
        super(DEFAULT_MESSAGE);
    }
}
