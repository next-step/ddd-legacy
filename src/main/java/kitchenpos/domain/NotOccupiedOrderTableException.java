package kitchenpos.domain;

import java.util.UUID;

public class NotOccupiedOrderTableException extends IllegalStateException {
    private static final String MESSAGE = "착석상태가 아닌 주문테이블입니다. OrderTable id 값: [%s]";

    public NotOccupiedOrderTableException(UUID orderTableId) {
        super(String.format(MESSAGE, orderTableId));
    }
}
