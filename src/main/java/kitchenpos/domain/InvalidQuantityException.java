package kitchenpos.domain;

public class InvalidQuantityException extends IllegalArgumentException {
    private static final String MESSAGE = "수량은 음수일 수 없습니다. 현재 값: [%s]";

    public InvalidQuantityException(long quantity) {
        super(String.format(MESSAGE, quantity));
    }
}
