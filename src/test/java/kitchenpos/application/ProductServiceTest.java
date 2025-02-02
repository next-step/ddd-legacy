package kitchenpos.application;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
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
import static org.mockito.BDDMockito.given;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @MockBean
    private PurgomalumClient purgomalumClient;

    @Nested
    @DisplayName("상품 등록")
    class RegisterProduct {

        @Test
        @DisplayName("상품을 등록한다.")
        void testRegisterProduct() {
            // given
            final Product request = ProductFixture.createProductRequest("후라이드", BigDecimal.valueOf(16_000));
            given(purgomalumClient.containsProfanity(anyString())).willReturn(false);

            // when
            final Product result = productService.create(request);

            // then
            final Product found = productRepository.findById(result.getId()).orElse(null);

            assertThat(found).isNotNull();
            assertAll(
                    () -> assertThat(found.getName()).isEqualTo(request.getName()),
                    () -> assertThat(found.getPrice()).isEqualByComparingTo(request.getPrice())
            );
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("상품은 이름과 가격을 필수로 가진다.")
        void testNullName(final String name) {
            // given
            final Product request = ProductFixture.createProductRequest(name, BigDecimal.valueOf(16_000));
            given(purgomalumClient.containsProfanity(anyString())).willReturn(false);

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
            final Product request = ProductFixture.createProduct("후라이드", BigDecimal.valueOf(price));
            given(purgomalumClient.containsProfanity(anyString())).willReturn(false);

            // when & then
            assertThatException()
                    .isThrownBy(() -> productService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("상품 등록 시 이름의 유해성 여부를 검사한다.")
        void testInappropriateName() {
            // given
            final Product request = ProductFixture.createProduct("부적절한이름", BigDecimal.valueOf(1000));
            given(purgomalumClient.containsProfanity(anyString())).willReturn(true);

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
            existingId = saveProduct().getId();
        }

        @Test
        @DisplayName("지정한 상품의 가격을 변경할 수 있다.")
        void changeProductPriceSuccess() {
            // given
            final Product request = ProductFixture.createProductRequest("후라이드", BigDecimal.valueOf(20_000));

            // when
            final Product result = productService.changePrice(existingId, request);

            // then
            final Product found = productRepository.findById(result.getId()).orElse(null);

            assertThat(found).isNotNull();
            assertThat(found.getPrice()).isEqualByComparingTo(request.getPrice());
        }

        @ParameterizedTest
        @DisplayName("상품 가격 변경 시 가격이 0원 이상이어야 한다.")
        @ValueSource(ints = {-1000, -1})
        void changeProductPriceFailsWithNegativePrice(final int price) {
            // given
            final Product request = ProductFixture.createProductRequest("후라이드", BigDecimal.valueOf(price));

            // when & then
            assertThatException()
                    .isThrownBy(() -> productService.changePrice(existingId, request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("등록되지 않은 상품의 가격을 변경할 수 없다.")
        void testNonExistingProduct() {
            // given
            final Product request = ProductFixture.createProductRequest("후라이드", BigDecimal.valueOf(20_000));

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
            List<UUID> ids = List.of(saveProduct(1).getId(), saveProduct(2).getId());

            // when
            final List<Product> result = productService.findAll();

            // then
            assertThat(result)
                    .hasSize(2)
                    .extracting(Product::getId)
                    .containsExactly(ids.toArray(UUID[]::new));
        }
    }

    private Product saveProduct(int index) {
        final Product product = ProductFixture.createProduct("후라이드%d".formatted(index) + index, BigDecimal.valueOf(16_000));
        return productRepository.save(product);
    }

    private Product saveProduct() {
        return saveProduct(1);
    }

}
