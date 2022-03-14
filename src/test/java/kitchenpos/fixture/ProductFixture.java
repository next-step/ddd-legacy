package kitchenpos.fixture;

import kitchenpos.domain.Product;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * <pre>
 * kitchenpos.fixture
 *      ProductFixture
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-15 오전 12:00
 */

public class ProductFixture {

    public static Product normalProduct() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("아이스 아메리카노");
        product.setPrice(new BigDecimal(5000L));
        return product;
    }

    public static Product emptyProductName() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(new BigDecimal(5000L));
        return product;
    }

    public static Product slangProductName() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("맛있는 Fuck");
        product.setPrice(new BigDecimal(5000L));
        return product;
    }

    public static Product wrongPriceProduct() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("아이스 아메리카노");
        product.setPrice(new BigDecimal(-1000L));
        return product;
    }
}
