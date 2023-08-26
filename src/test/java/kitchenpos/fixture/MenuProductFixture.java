package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;

public class MenuProductFixture {

    private MenuProductFixture() {
    }

    public static MenuProduct create(Product product, int quantity) {
        MenuProduct result = new MenuProduct();
        result.setProduct(product);
        result.setQuantity(quantity);
        result.setProductId(product.getId());
        return result;
    }

}
