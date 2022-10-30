
package kitchenpos.product;

import kitchenpos.common.vo.Name;
import kitchenpos.common.vo.Price;
import kitchenpos.product.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    public static Product product(UUID id, BigDecimal productPrice) {
        return new Product(id, new Name("상푸명", false), new Price(productPrice));
    }
}


