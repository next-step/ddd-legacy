package kitchenpos;

import java.math.BigDecimal;
import kitchenpos.domain.Product;

public class ProductTestFixture {
    private ProductTestFixture() { }

    public static Product createProductRequest(String productName, BigDecimal productPrice) {
        Product product = new Product();
        product.setName(productName);
        product.setPrice(productPrice);
        return product;
    }

}
