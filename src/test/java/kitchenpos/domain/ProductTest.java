package kitchenpos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class ProductTest {
    @Test
    @DisplayName("상품은 이름과 가격을 가지고 있다.")
    void create() {
        UUID id = UUID.randomUUID();
        BigDecimal price = new BigDecimal("1000");

        Product product = new Product();
        product.setId(id);
        product.setName("치킨");
        product.setPrice(price);

        assertThat(product.getId()).isEqualTo(id);
    }
}
