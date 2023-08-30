package kitchenpos.dummy;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.util.List;

public class DummyMenuProduct {

    public static MenuProduct createMenuProduct(Product product, Long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(1L);
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(product.getId());
        return menuProduct;
    }

    public static List<MenuProduct> defaultMenuProducts(Product product, Long quantity) {
        return List.of(createMenuProduct(product, quantity));
    }
    public static List<MenuProduct> defaultMenuProducts() {
        return List.of(createMenuProduct(DummyProduct.createProductRequest(), 1L));
    }
    public static MenuProduct defaultMenuProduct() {
        return createMenuProduct(DummyProduct.createProductRequest(), 1L);
    }
}
