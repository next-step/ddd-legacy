package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ProductFixture {

    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final String PRODUCT_NAME = "name";
    private static final BigDecimal PRODUCT_PRICE = BigDecimal.TEN;

    public static Product createProduct() {
        Product product = new Product();
        product.setId(PRODUCT_ID);
        product.setName(PRODUCT_NAME);
        product.setPrice(PRODUCT_PRICE);

        return product;
    }

    public static Product createProductWithName(final String name) {
        Product product = createProduct();
        product.setName(name);

        return product;
    }

    public static Product createProductWithPrice(final BigDecimal price) {
        Product product = createProduct();
        product.setPrice(price);

        return product;
    }

    public static List<Product> createProducts() {
        return List.of(createProduct(), createProduct());
    }

}
