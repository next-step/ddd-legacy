package kitchenpos.product.domain;

import kitchenpos.menu.menu.domain.Price;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("상품")
class ProductTest {

    @DisplayName("상품 가격은 0원보다 작을 수 없다.")
    @Test
    void productPriceOverZero() {
        assertThatThrownBy(() -> new Product(new Price(BigDecimal.valueOf(-1))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격은 0원보다 커야합니다.");
    }
}
