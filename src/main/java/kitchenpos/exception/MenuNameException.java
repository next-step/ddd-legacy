package kitchenpos.exception;

public class MenuNameException extends IllegalArgumentException {
    public MenuNameException(final String message) {
        super(message);
    }
}
