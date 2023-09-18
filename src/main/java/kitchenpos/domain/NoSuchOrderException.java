package kitchenpos.domain;

import java.util.NoSuchElementException;
import java.util.UUID;

public class NoSuchOrderException extends NoSuchElementException {
    private static final String MESSAGE = "해당 주문이 존재하지 않습니다. Order id 값: [%s]";

    public NoSuchOrderException(UUID orderId) {
        super(String.format(MESSAGE, orderId));
    }
}
