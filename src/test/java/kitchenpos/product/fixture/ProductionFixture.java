package kitchenpos.product.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductionFixture {

    public static Product createProduct(String name, int price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    public static Product 상품_요청(String name, int price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    public static Product 상품(String name, int price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
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
