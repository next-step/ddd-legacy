package kitchenpos.fixture;

import kitchenpos.domain.Product;

<<<<<<< HEAD
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
=======
import javax.persistence.Column;
import java.math.BigDecimal;
>>>>>>> 089fb8acd7302a8603969a0abbd6bfd510ebb2f4
import java.util.UUID;

/**
 * <pre>
 * kitchenpos.fixture
 *      ProductFixture
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
<<<<<<< HEAD
 * @since 2022-03-20 오후 11:49
=======
 * @since 2022-03-15 오전 12:00
>>>>>>> 089fb8acd7302a8603969a0abbd6bfd510ebb2f4
 */

public class ProductFixture {

<<<<<<< HEAD
    public static Product 상품_가격_이름_NULL() {
        return new Product(UUID.randomUUID(), null, null);
    }

    public static Product 상품_가격_0원_미만() {
        return new Product(UUID.randomUUID(), "후라이드", BigDecimal.valueOf(-5000L));
    }

    public static Product 상품() {
        return new Product(UUID.randomUUID(), "후라이드", BigDecimal.valueOf(16000L));
    }

    public static Product 상품(UUID uuid, String name, BigDecimal price) {
        return new Product(uuid, name, price);
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
=======
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
>>>>>>> 089fb8acd7302a8603969a0abbd6bfd510ebb2f4
    }
}
