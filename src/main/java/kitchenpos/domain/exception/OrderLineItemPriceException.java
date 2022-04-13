package kitchenpos.domain.exception;

public class OrderLineItemPriceException extends IllegalArgumentException {

    public OrderLineItemPriceException(String menu, long menuPrice, long requestPrice) {
        super("가격이 일치하지 않습니다. 메뉴명: " + menu + ", 메뉴 가격: " + menuPrice + ", 지불 가격: " + requestPrice);
    }
}
