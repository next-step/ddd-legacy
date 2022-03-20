package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * kitchenpos.fixture
 *      ProductFixture
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-20 오후 11:49
 */

public class ProductFixture {

    public static Product 상품_가격_이름_NULL() {
        return new Product(UUID.randomUUID(), null, null);
    }

    public static Product 상품_가격_0원_미만() {
        return new Product(UUID.randomUUID(), "후라이드", BigDecimal.valueOf(-5000L));
    }

    public static Product 상품() {
        return new Product(UUID.randomUUID(), "후라이드", BigDecimal.valueOf(16000L));
    }

    public static List<Product> 상품_목록() {
        return Arrays.asList(
            new Product(UUID.randomUUID(), "후라이드", BigDecimal.valueOf(16000L)),
            new Product(UUID.randomUUID(), "양념치킨", BigDecimal.valueOf(16000L)),
            new Product(UUID.randomUUID(), "반반치킨", BigDecimal.valueOf(16000L))
        );
    }

    public static Product 변경_상품(UUID uuid) {
        return new Product(uuid, "후라이드", BigDecimal.valueOf(16000L));
    }

    public static Product 변경_값() {
        return new Product(null, "순살치킨", BigDecimal.valueOf(17000L));
    }
}
