package kitchenpos.application;

import static kitchenpos.domain.ProductFixture.Product;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

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
}
