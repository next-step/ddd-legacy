package kitchenpos.application.testFixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductFixture() {

    public static Product newOne() {
        var product = new Product();
        product.setName("닭고기 300g");
        product.setPrice(BigDecimal.valueOf(5000));
        return product;
    }

    public static Product newOnById(UUID id) {
        var product = new Product();
        product.setId(id);
        product.setName("닭고기 300g");
        product.setPrice(BigDecimal.valueOf(5000));
        return product;
    }
}
