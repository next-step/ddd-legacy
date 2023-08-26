package kitchenpos.testHelper.fixture;

import java.math.BigDecimal;
import kitchenpos.domain.Product;

public class ProductFixture {

    public static Product forPrice(long price) {
        return new Product("name", BigDecimal.valueOf(price));
    }

    public static Product of(String name, long price) {
        return new Product(name, BigDecimal.valueOf(price));
    }

}
