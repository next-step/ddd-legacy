package kitchenpos.application.fixture;

import io.micrometer.common.util.StringUtils;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
    public static Product setProduct(String name, String price) {
        Product product = new Product();

        product.setId(UUID.randomUUID());
        product.setName(name);
        if(!StringUtils.isBlank(price)) {
            product.setPrice(BigDecimal.valueOf(Double.parseDouble(price)));
        }

        return product;
    }



}
