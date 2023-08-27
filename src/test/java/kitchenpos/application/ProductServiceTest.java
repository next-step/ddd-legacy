package kitchenpos.application;

import kitchenpos.domain.Product;
import kitchenpos.helper.ProductHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductServiceTest extends ApplicationTest {

    @Autowired
    private ProductService productService;


    @DisplayName("새로운 상품을 등록한다.")
    @Nested
    class CreateProduct {

        @DisplayName("상품에 대한 가격은 0원 이상이어야 한다.")
        @Nested
        class Policy1 {
            @DisplayName("상품에 대한 가격은 0원 이상인 경우 (성공)")
            @ParameterizedTest
            @ValueSource(ints = {0, 1, Integer.MAX_VALUE})
            void success1(final int priceInt) {
                // Given
                BigDecimal price = new BigDecimal(priceInt);
                Product product = ProductHelper.create(price);

                // When
                Product createdProduct = productService.create(product);

                // Then
                assertThat(createdProduct.getPrice()).isEqualTo(price);
            }

            @DisplayName("상품에 대한 가격은 null 인 경우 (실패)")
            @ParameterizedTest
            @NullSource
            void fail1(BigDecimal price) {
                // When
                Product product = ProductHelper.create(price);

                // Then
                assertThatThrownBy(() -> productService.create(product))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("상품에 대한 가격은 0원 미만인 경우 (실패)")
            @ParameterizedTest
            @ValueSource(ints = {-1, -100, Integer.MIN_VALUE})
            void fail2(final int priceInt) {
                // Given
                BigDecimal price = new BigDecimal(priceInt);

                // When
                Product product = ProductHelper.create(price);

                // Then
                assertThatThrownBy(() -> productService.create(product))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

    }

}