package kitchenpos.exception;

public class ProductNameException extends IllegalArgumentException {
    public ProductNameException(final String message) {
        super(message);
    }
}
