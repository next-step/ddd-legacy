package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.util.UUID;

/**
 * <pre>
 * kitchenpos.fixture
 *      MenuProductFixture
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-21 오전 2:00
 */

public class MenuProductFixture {
    public static MenuProduct 메뉴_상품(Long id, Product product, long quantity, UUID productId) {
        return new MenuProduct(id, product, quantity, productId);
    }

    public static MenuProduct 메뉴_상품(UUID productId, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(ProductFixture.상품());
        menuProduct.setProductId(productId);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
