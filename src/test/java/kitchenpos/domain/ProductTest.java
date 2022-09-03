package kitchenpos.domain;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ProductTest {
    @DisplayName("상품은 이름과 가격을 가지고 있다.")
    @Test
    void properties() {
        final var product = new Product();

        product.setName("이름");
        product.setPrice(new BigDecimal(1000));

        assertAll(
                () -> assertThat(product.getName()).isEqualTo("이름"),
                () -> assertThat(product.getPrice()).isEqualTo(new BigDecimal(1000))
        );
    }
}
