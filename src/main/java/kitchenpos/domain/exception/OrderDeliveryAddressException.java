package kitchenpos.domain.exception;

public class OrderDeliveryAddressException extends IllegalArgumentException {

    public OrderDeliveryAddressException() {
        super("배달 주소가 없습니다.");
    }
}
