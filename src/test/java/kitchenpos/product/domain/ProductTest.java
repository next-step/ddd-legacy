package kitchenpos.product.domain;

import kitchenpos.domain.Name;
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
        assertThatThrownBy(() -> new Product(new Name("상품명", false), new Price(BigDecimal.valueOf(-1))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격은 0원보다 커야합니다.");
    }

    @DisplayName("상품 가격은 필수이다.")
    @Test
    void requireProductPrice() {
        assertThatThrownBy(() -> new Product(new Name("상품명", false), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품 가격을 입력해주세요.");
    }

    @DisplayName("상품명은 필수이다.")
    @Test
    void requireProductName() {
        assertThatThrownBy(() -> new Product(null, new Price(BigDecimal.ONE)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품명은 필수입니다.");
    }

    @DisplayName("상품명은 비속어를 사용할 수 없다.")
    @Test
    void profanity() {
        assertThatThrownBy(() -> new Product(new Name("상품명", true), new Price(BigDecimal.ONE)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비속어를 포함할 수 없습니다.");
    }
}
