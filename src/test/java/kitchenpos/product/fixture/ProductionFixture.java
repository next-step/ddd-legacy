package kitchenpos.product.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;

public class ProductionFixture {

    public static ProductSaveRequest createProductSaveRequest(final String name, final int price) {
        return new ProductSaveRequest(name, price);
    }

    public static Product createProduct(String name, int price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    public static MenuProduct createMenuProduct(final Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(quantity);
        menuProduct.setProduct(product);
        return menuProduct;
    }
}
