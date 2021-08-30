package kitchenpos.commons;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.springframework.stereotype.Component;

@Component
public class MenuProductGenerator {

    public MenuProduct generateByProduct(Product product) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(2);
        menuProduct.setProductId(product.getId());
        return menuProduct;
    }

}
