package kitchenpos.fixture;

import jakarta.validation.constraints.NotNull;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

import static kitchenpos.MoneyConstants.만원;

public class ProductFixture {

    public static final String 상품명 = "상품명";

    public static Product createProductWithoutName() {
        return createProduct(null, 만원);
    }

    public static Product createProduct() {
        return createProduct(상품명, 만원);
    }

    public static Product createProduct(long price) {
        return createProduct(상품명, price);
    }

    public static Product createProduct(String name) {
        return createProduct(name, 만원);
    }

    public static @NotNull Product createProduct(String name, long price) {
        final var product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }
}
