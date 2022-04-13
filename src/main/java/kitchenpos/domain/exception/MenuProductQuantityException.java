package kitchenpos.domain.exception;

public class MenuProductQuantityException extends IllegalArgumentException {

    public MenuProductQuantityException(long quantity) {
        super("주문하는 상품의 수량은 0 이상이어야 합니다. 주문하려는 상품의 수량 : " + quantity);
    }
}
