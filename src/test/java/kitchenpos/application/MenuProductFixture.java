package kitchenpos.application;

import static kitchenpos.application.ProductFixture.맛초킹;
import static kitchenpos.application.ProductFixture.뿌링클;
import static kitchenpos.application.ProductFixture.콜라;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductFixture {

    public static final MenuProduct 뿌링클_1개 = new MenuProduct();
    public static final MenuProduct 맛초킹_1개 = new MenuProduct();
    public static final MenuProduct 콜라_1개 = new MenuProduct();
    public static final MenuProduct 콜라_수량_오류 = new MenuProduct();

    static {
        initialize(뿌링클_1개, 뿌링클);
        initialize(맛초킹_1개, 맛초킹);
        initialize(콜라_1개, 콜라);
        initialize(콜라_수량_오류, 콜라, -1L);
    }

    private static void initialize(MenuProduct menuProduct, Product product) {
        initialize(menuProduct, product, 1L);
    }

    private static void initialize(MenuProduct menuProduct, Product product, long quantity) {
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
    }

}
