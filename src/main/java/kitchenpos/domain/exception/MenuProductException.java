package kitchenpos.domain.exception;

public class MenuProductException extends IllegalArgumentException {

    public MenuProductException(int requestProductSize, int productSize) {
        super("주문하는 메뉴 상품과 실제 메뉴 상품의 수가 일치하지 않습니다. 주문 : " + requestProductSize + ", 실제 : " + productSize);
    }
}
