package kitchenpos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ProductTest {
    @DisplayName("상품 생성")
    @Test
    void test1() {
        final UUID id = UUID.randomUUID();
        final String name = "치킨";
        final BigDecimal price = BigDecimal.TEN;

        final Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);

        assertAll(
                () -> assertThat(product.getId()).isEqualTo(id),
                () -> assertThat(product.getName()).isEqualTo(name),
                () -> assertThat(product.getPrice()).isEqualTo(price)
        );
    }
}