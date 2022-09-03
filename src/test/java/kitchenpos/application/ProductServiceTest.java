package kitchenpos.application;

import static kitchenpos.domain.ProductFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("새로운 상품을 생성할 수 있다.")
    @Nested
    class Create {

        @DisplayName("성공")
        @Test
        void create() {
            // given
            Product request = Product("후라이드 치킨", 15_000);

            // when
            Product savedProduct = productService.create(request);

            // then
            assertThat(savedProduct.getId()).isNotNull();
            assertThat(savedProduct.getName()).isEqualTo("후라이드 치킨");
            assertThat(savedProduct.getPrice()).isEqualTo(BigDecimal.valueOf(15_000));
        }

        @DisplayName("상품 금액은 null 일 수 없다.")
        @Test
        void priceNullException() {
            // given
            Product request = new Product();
            request.setName("후라이드 치킨");
            request.setPrice(null);

            // when, then
            assertThatThrownBy(() -> productService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("상품 금액은 음수일 수 없다.")
        @Test
        void priceNegativeException() {
            // given
            Product request = Product("후라이드 치킨", -1);

            // when, then
            assertThatThrownBy(() -> productService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("상품 이름은 null 일 수 없다.")
        @Test
        void nameNullException() {
            // given
            Product request = Product(null, 15_000);

            // when, then
            assertThatThrownBy(() -> productService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("상품 이름은 욕설을 포함할 수 없다.")
        @Test
        void nameProfanityException() {
            // given
            Product request = Product("ass 후라이드 치킨", 15_000);

            // when, then
            assertThatThrownBy(() -> productService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("상품의 금액을 변경할 수 있다.")
    @Nested
    class ChangePrice {

        @DisplayName("성공")
        @Test
        void changePrice() {
            // given
            Product 후라이드_치킨 = productRepository.save(ProductWithUUID("후라이드 치킨", 15_000));
            Product request_16000원 = Product("후라이드 치킨", 16_000);

            // when
            Product result = productService.changePrice(
                후라이드_치킨.getId(),
                request_16000원
            );

            // then
            assertThat(result.getId()).isEqualTo(후라이드_치킨.getId());
            assertThat(result.getName()).isEqualTo("후라이드 치킨");
            assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(16_000));
        }

        @DisplayName("상품이 우선 존재해야 한다.")
        @Test
        void productNotFoundException() {
            // given
            Product request_16000원 = Product("후라이드 치킨", 16_000);

            // when, then
            assertThatThrownBy(() -> productService.changePrice(
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                request_16000원
            )).isExactlyInstanceOf(NoSuchElementException.class);
        }
    }
}
