package kitchenpos.application;

import kitchenpos.domain.Product;
import kitchenpos.helper.ProductHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static kitchenpos.helper.ProductHelper.DEFAULT_PRICE;
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

        @DisplayName("상품명은 비어있을 수 없고, 255자를 초과할 수 없다.")
        @Nested
        class Policy2 {
            @DisplayName("상품명이 0자 이상 255자 이하인 경우 (성공)")
            @ParameterizedTest
            @ValueSource(strings = {"", "한", "a", "1", "상품명", "product name", "상품 A", "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one."})
            void success1(final String name) {
                // Given
                Product product = ProductHelper.create(name);

                // When
                Product createdProduct = productService.create(product);

                // Then
                assertThat(createdProduct.getName()).isEqualTo(name);
                assertThat(createdProduct.getPrice()).isEqualTo(DEFAULT_PRICE);
            }

            @DisplayName("상품명이 null 인 경우 (실패)")
            @ParameterizedTest
            @NullSource
            void fail1(final String name) {
                // When
                Product product = ProductHelper.create(name);

                // Then
                assertThatThrownBy(() -> productService.create(product))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("상품명이 255자를 초과한 경우 (실패)")
            @ParameterizedTest
            @ValueSource(strings = {"Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one ."})
            void fail2(final String name) {
                // When
                Product product = ProductHelper.create(name);

                // Then
                assertThatThrownBy(() -> productService.create(product))
                        .isInstanceOf(DataIntegrityViolationException.class);
            }
        }

    }

}