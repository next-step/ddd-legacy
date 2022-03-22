package kitchenpos.exception;

public class OrderTableIsEmptyException extends IllegalArgumentException {
    public OrderTableIsEmptyException(final String message) {
        super(message);
    }
}
