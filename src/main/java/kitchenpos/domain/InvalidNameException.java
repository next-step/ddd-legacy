package kitchenpos.domain;

public class InvalidNameException extends IllegalArgumentException {
    private static final String MESSAGE = "이름은 null이거나 비어있을 수 없습니다. 현재 값: [%s]";

    public InvalidNameException(String message) {
        super(String.format(MESSAGE, message));
    }
}
