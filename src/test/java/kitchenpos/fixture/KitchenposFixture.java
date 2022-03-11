package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class KitchenposFixture {

    public static Product createProduct(String name, BigDecimal price) {
        Product 상품 = new Product();
        상품.setPrice(price);
        상품.setName(name);
        상품.setId(UUID.randomUUID());
        return 상품;
    }

}
