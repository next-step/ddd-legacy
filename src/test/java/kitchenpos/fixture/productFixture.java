package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

import static kitchenpos.TestConstant.*;

public class productFixture {

    public static Product createProduct() {
        Product product = new Product();
        product.setId(후라이드치킨_PRODUCT_UUID);
        product.setName(TEST_PRODUCT_NAME);
        product.setPrice(후라이드치킨_DEFAULT_PRICE);
        return product;
    }

    public static Product createProduct(BigDecimal bigDecimalMinusOne) {
        Product product = new Product();
        product.setId(후라이드치킨_PRODUCT_UUID);
        product.setName(TEST_PRODUCT_NAME);
        product.setPrice(BIG_DECIMAL_MINUS_ONE);
        return product;
    }

    public static Product createProduct(final UUID uuid, final String name, final BigDecimal price) {
        Product product = new Product();
        product.setId(uuid);
        product.setName(name);
        product.setPrice(price);
        return product;
    }
}
