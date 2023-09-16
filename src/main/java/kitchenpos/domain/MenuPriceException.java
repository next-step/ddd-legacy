package kitchenpos.domain;

import java.math.BigDecimal;

public class MenuPriceException extends RuntimeException {
    private static final String MESSAGE = "메뉴가격은 속한 메뉴상품 가격의 총합보다 클 수 없습니다. 메뉴가격: %s, 메뉴상품가격총합: %s";

    public MenuPriceException(BigDecimal menuPrice, BigDecimal menuProductsTotalPrice) {
        super(String.format(MESSAGE, menuPrice, menuProductsTotalPrice));
    }
}
