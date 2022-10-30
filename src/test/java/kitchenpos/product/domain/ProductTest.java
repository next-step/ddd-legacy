package kitchenpos.product.domain;

import kitchenpos.common.vo.Name;
import kitchenpos.common.vo.Price;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("상품")
class ProductTest {

    @DisplayName("상품 가격은 0원보다 작을 수 없다.")
    @ParameterizedTest
    @CsvSource({"상품명, false, -1"})
    void productPriceOverZero(String productName, boolean isProfanity, BigDecimal productPrice) {
        assertThatThrownBy(() -> product(new Name(productName, isProfanity), new Price(productPrice)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격은 0원보다 커야합니다.");
    }

    @DisplayName("상품 가격은 필수이다.")
    @ParameterizedTest
    @CsvSource({"상품명, false"})
    void requireProductPrice(String productName, boolean isProfanity) {
        assertThatThrownBy(() -> product(new Name(productName, isProfanity), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품 가격을 입력해주세요.");
    }

    @DisplayName("상품명은 필수이다.")
    @ParameterizedTest
    @CsvSource({"1"})
    void requireProductName(BigDecimal productPrice) {
        assertThatThrownBy(() -> product(null, new Price(productPrice)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품명은 필수입니다.");
    }

    @DisplayName("상품명은 비속어를 사용할 수 없다.")
    @ParameterizedTest
    @CsvSource({"상품명, true, 1"})
    void profanity(String productName, boolean isProfanity, BigDecimal productPrice) {
        assertThatThrownBy(() -> product(new Name(productName, isProfanity), new Price(productPrice)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비속어를 포함할 수 없습니다.");
    }

    @DisplayName("상품 가격을 0원보다 작은 가격으로 변경 할 수 없다.")
    @ParameterizedTest
    @CsvSource({"상품명, false, 1, -1"})
    void changeMinimumPrice(String productName, boolean isProfanity, BigDecimal productPrice, BigDecimal changePrice) {
        Product product = product(new Name(productName, isProfanity), new Price(productPrice));
        assertThatThrownBy(() -> product.changePrice(changePrice))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("가격은 0원보다 커야합니다.");
    }

    @DisplayName("상품 가격을 필수로 입력받는다.")
    @ParameterizedTest
    @CsvSource({"상품명, false, 1"})
    void changeEmptyPrice(String productName, boolean isProfanity, BigDecimal productPrice) {
        Product product = product(new Name(productName, isProfanity), new Price(productPrice));
        assertThatThrownBy(() -> product.changePrice(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null 일 수 없습니다.");
    }

    @DisplayName("상품을 생성할 수 있다.")
    @ParameterizedTest
    @CsvSource({"상품명, false, 1, 10"})
    void createProduct(String productName, boolean isProfanity, BigDecimal productPrice) {
        assertThatNoException().isThrownBy(() -> product(new Name(productName, isProfanity), new Price(productPrice)));
    }

    @DisplayName("상품 가격을 변경할 수 있다.")
    @ParameterizedTest
    @CsvSource({"상품명, false, 1, 10"})
    void changePrice(String productName, boolean isProfanity, BigDecimal productPrice, BigDecimal changePrice) {
        Product product = product(new Name(productName, isProfanity), new Price(productPrice));
        product.changePrice(changePrice);
        assertThat(product.getPrice()).isEqualTo(changePrice);
    }

    private static Product product(Name productName, Price price) {
        return new Product(UUID.randomUUID(), productName, price);
    }
}
