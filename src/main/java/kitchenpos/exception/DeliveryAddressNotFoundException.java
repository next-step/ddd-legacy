package kitchenpos.exception;

public class DeliveryAddressNotFoundException extends IllegalArgumentException {
    public DeliveryAddressNotFoundException(final String message) {
        super(message);
    }
}
