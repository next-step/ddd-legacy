package kitchenpos.commons;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.springframework.stereotype.Component;

@Component
public class MenuProductGenerator {

    public MenuProduct generateRequestByProduct(Product product) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(1L);
        menuProduct.setQuantity(2);
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        return menuProduct;
    }

}
