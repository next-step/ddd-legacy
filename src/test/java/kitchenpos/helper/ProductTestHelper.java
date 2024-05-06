package kitchenpos.helper;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductTestHelper {
    public static Product 음식_생성(String name, BigDecimal price){
        UUID id = UUID.randomUUID();

        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);

        return product;
    }
}
