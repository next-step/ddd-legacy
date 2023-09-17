package kitchenpos.domain;

import java.util.NoSuchElementException;
import java.util.UUID;

public class NoSuchOrderTableException extends NoSuchElementException {
    private static final String MESSAGE = "해당 OrderTable이 존재하지 않습니다. OrderTable id 값: [%s]";

    public NoSuchOrderTableException(UUID orderTableId) {
        super(String.format(MESSAGE, orderTableId));
    }
}
