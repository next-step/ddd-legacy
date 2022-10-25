package kitchenpos.product.domain;

import kitchenpos.common.vo.Name;
import kitchenpos.common.vo.Price;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

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

    @DisplayName("상품 가격을 0원보다 작은 가격으로 변경 할 수 없다.")
    @Test
    void changeMinimumPrice() {
        Product product = new Product(new Name("상품명", false), new Price(BigDecimal.ONE));
        assertThatThrownBy(() -> product.changePrice(BigDecimal.valueOf(-1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격은 0원보다 커야합니다.");
    }

    @DisplayName("상품 가격을 필수로 입력받는다.")
    @Test
    void changeEmptyPrice() {
        Product product = new Product(new Name("상품명", false), new Price(BigDecimal.ONE));
        assertThatThrownBy(() -> product.changePrice(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null 일 수 없습니다.");
    }

    @DisplayName("상품을 생성할 수 있다.")
    @Test
    void createProduct() {
        assertThatNoException().isThrownBy(() -> new Product(new Name("상품명", false), new Price(BigDecimal.ONE)));
    }

    @DisplayName("상품 가격을 변경할 수 있다.")
    @Test
    void changePrice() {
        Product product = new Product(new Name("상품명", false), new Price(BigDecimal.ONE));
        product.changePrice(BigDecimal.TEN);
        assertThat(product.getPrice()).isEqualTo(BigDecimal.TEN);
    }
}
