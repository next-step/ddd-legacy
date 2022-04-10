package kitchenpos.domain.exception;

import java.math.BigDecimal;

public class MenuMarginException extends IllegalArgumentException {

    public MenuMarginException(BigDecimal price, BigDecimal sum) {
        super("메뉴의 가격은 주문하는 상품들의 가격의 합보다 작아야 합니다. "
            + "가격 : " + price.longValue() + ", 주문하는 상품들의 가격의 합 : " + sum.longValue());
    }
}
