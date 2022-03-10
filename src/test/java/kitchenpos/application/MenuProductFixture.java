package kitchenpos.application;

import static kitchenpos.application.ProductFixture.뿌링클;
import static kitchenpos.application.ProductFixture.콜라;

import kitchenpos.domain.MenuProduct;

public class MenuProductFixture {

    public static final MenuProduct 메뉴_뿌링클 = new MenuProduct();
    public static final MenuProduct 메뉴_콜라 = new MenuProduct();

    static {
        메뉴_뿌링클.setProduct(뿌링클);
        메뉴_뿌링클.setQuantity(1L);
        메뉴_콜라.setProduct(콜라);
        메뉴_콜라.setQuantity(1L);
    }

}
