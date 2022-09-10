package kitchenpos.fixture;

import java.math.BigDecimal;
import kitchenpos.domain.Product;

public class ProductFixture {

    public static Product createProduct() {
        Product product = new Product();
        product.setName("상품");
        product.setPrice(BigDecimal.valueOf(1000));
        return product;
    }

    public static Product createProduct(BigDecimal price) {
        Product product = createProduct();
        product.setPrice(price);
        return product;
    }

    public static Product createProduct(String name) {
        Product product = createProduct();
        product.setName(name);
        return product;
    }

}
