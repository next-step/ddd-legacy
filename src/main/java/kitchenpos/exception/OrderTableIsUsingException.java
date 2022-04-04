package kitchenpos.exception;

public class OrderTableIsUsingException extends IllegalArgumentException {
    public OrderTableIsUsingException(final String message) {
        super(message);
    }
}
