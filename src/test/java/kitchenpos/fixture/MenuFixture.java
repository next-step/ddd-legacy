package kitchenpos.fixture;

import helper.PriceGenerator;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuFixture {

    public static Menu.Builder aMenuRequest() {
        return Menu.builder()
                .name("후라이드 치킨 메뉴")
                .price(new PriceGenerator().of(16000))
                .displayed(true);
    }

    //MenuProduct
    public static MenuProduct.Builder aMenuProductRequest(Product product) {
        return MenuProduct.builder()
                .productId(product.getId())
                .product(product)
                .quantity(1L);
    }


}
