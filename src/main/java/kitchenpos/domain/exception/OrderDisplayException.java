package kitchenpos.domain.exception;

public class OrderDisplayException extends IllegalStateException {

    public OrderDisplayException() {
        super("진열되지 않은 메뉴는 주문할 수 없습니다.");
    }
}
