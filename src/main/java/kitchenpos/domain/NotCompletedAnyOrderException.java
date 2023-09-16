package kitchenpos.domain;

import java.util.UUID;

public class NotCompletedAnyOrderException extends IllegalStateException {
    private static final String MESSAGE = "주문테이블에 완료되지 않은 주문이 존재합니다. 현재 값: [%s]";

    public NotCompletedAnyOrderException(UUID orderTableId) {
        super(String.format(MESSAGE, orderTableId));
    }
}
