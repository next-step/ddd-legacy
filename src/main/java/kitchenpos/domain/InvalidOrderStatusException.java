package kitchenpos.domain;

public class InvalidOrderStatusException extends IllegalStateException {
    private static final String MESSAGE = "현재 주문 상태에서는 요청한 주문 상태로 바뀔 수 없습니다. 현재 주문 상태: [%s], 요청한 주문 상태: [%s]";


    public InvalidOrderStatusException(OrderStatus status, OrderStatus requestedStatus) {
        super(String.format(MESSAGE, status, requestedStatus));
    }
}
