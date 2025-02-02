package kitchenpos.application;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @MockBean
    private PurgomalumClient purgomalumClient;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Nested
    @DisplayName("상품 등록")
    class RegisterProduct {

        @Test
        @DisplayName("상품을 등록한다.")
        void testRegisterProduct() {
            // given
            final String name = "PRODUCT_NAME";
            final BigDecimal price = BigDecimal.valueOf(1000);
            final Product request = createProduct(name, price);
            when(purgomalumClient.containsProfanity(anyString())).thenReturn(false);

            // when
            final Product result = productService.create(request);

            // then
            final Product found = productRepository.findById(result.getId()).orElse(null);

            assertThat(found).isNotNull();
            assertAll(
                    () -> assertThat(found.getName()).isEqualTo(name),
                    () -> assertThat(found.getPrice()).isEqualByComparingTo(price)
            );
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("상품은 이름과 가격을 필수로 가진다.")
        void testNullOrEmptyName(final String name) {
            // given
            final Product request = createProduct(name, BigDecimal.valueOf(1000));
            when(purgomalumClient.containsProfanity(anyString())).thenReturn(false);

            // when & then
            assertThatException()
                    .isThrownBy(() -> productService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @DisplayName("상품의 가격은 0원 이상이어야 한다.")
        @ValueSource(ints = {-1000, -1})
        void testPriceLessThanZero(final int price) {
            // given
            final Product request = createProduct("VALID_NAME", BigDecimal.valueOf(price));
            when(purgomalumClient.containsProfanity(anyString())).thenReturn(false);

            // when & then
            assertThatException()
                    .isThrownBy(() -> productService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("상품 등록 시 이름의 유해성 여부를 검사한다.")
        void testInappropriateName() {
            // given
            final Product request = createProduct("INAPPROPRIATE_NAME", BigDecimal.valueOf(1000));
            when(purgomalumClient.containsProfanity(anyString())).thenReturn(true);

            // when & then
            assertThatException()
                    .isThrownBy(() -> productService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("상품 가격 변경")
    class ChangeProductPrice {
        UUID existingId;
        UUID nonExistingId = UUID.randomUUID();

        @BeforeEach
        void setup() {
            final Product product = createProduct("PRODUCT_NAME", BigDecimal.valueOf(1000));
            when(purgomalumClient.containsProfanity(anyString())).thenReturn(false);
            existingId = productService.create(product).getId();
        }

        @Test
        @DisplayName("지정한 상품의 가격을 변경할 수 있다.")
        void changeProductPriceSuccess() {
            // given
            final Product request = new Product();
            request.setPrice(BigDecimal.valueOf(2000));

            // when
            final Product result = productService.changePrice(existingId, request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(2000));
        }

        @ParameterizedTest
        @DisplayName("상품 가격 변경 시 가격이 0원 이상이어야 한다.")
        @ValueSource(ints = {-1000, -1})
        void changeProductPriceFailsWithNegativePrice() {
            // given
            final Product request = new Product();
            request.setPrice(BigDecimal.valueOf(-2000));

            // when & then
            assertThatException()
                    .isThrownBy(() -> productService.changePrice(existingId, request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("등록되지 않은 상품의 가격을 변경할 수 없다.")
        void testNonExistingProduct() {
            // given
            final Product request = new Product();
            request.setPrice(BigDecimal.valueOf(2000));

            // when & then
            assertThatException()
                    .isThrownBy(() -> productService.changePrice(nonExistingId, request))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("상품 조회")
    class FindAllProducts {

        @Test
        @DisplayName("등록된 모든 상품의 목록을 조회한다.")
        void findAllProductsSuccess() {
            // given
            final Product product1 = createProduct("PRODUCT_1", BigDecimal.valueOf(1000));
            final Product product2 = createProduct("PRODUCT_2", BigDecimal.valueOf(2000));
            when(purgomalumClient.containsProfanity(anyString())).thenReturn(false);
            productService.create(product1);
            productService.create(product2);

            // when
            final List<Product> result = productService.findAll();

            // then
            assertThat(result)
                    .hasSize(2)
                    .extracting(Product::getName)
                    .containsExactly("PRODUCT_1", "PRODUCT_2");
        }
    }

    private Product createProduct(final String name, final BigDecimal price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }

}
