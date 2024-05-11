package kitchenpos.application;

import kitchenpos.application.testFixture.MenuFixture;
import kitchenpos.application.testFixture.ProductFixture;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("상품(product) 서비스 테스트")
@Nested
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @Nested
    @DisplayName("상품의 가격을 변경시,")
    class ChangeProductPrice {

        @DisplayName("가격이 정상 변동된다.")
        @Test
        void changedPriceTest() {
            // given
            var id = UUID.randomUUID();
            var originalProduct = ProductFixture.newOne(id);
            var menu = MenuFixture.newOne(originalProduct);
            var updatedProduct = ProductFixture.newOne(4999);
            given(productRepository.findById(any())).willReturn(Optional.of(originalProduct));
            given(menuRepository.findAllByProductId(any())).willReturn(List.of(menu));

            // when
            var actual = productService.changePrice(id, updatedProduct);

            // then
            assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(4999));
        }
    }

    @Test
    void findAll() {
    }

    @Nested
    @DisplayName("상품 생성시,")
    class CreateProduct {

        @DisplayName("상품이 정상 생성된다.")
        @Test
        void createTest() {
            // given
            var product = ProductFixture.newOne(UUID.randomUUID());
            given(purgomalumClient.containsProfanity(any())).willReturn(false);
            given(productRepository.save(any())).willReturn(product);

            // when
            var actual = productService.create(product);

            // then
            assertThat(actual.getName()).isEqualTo("닭고기 300g");
        }

        @DisplayName("[예외] 상품의 가격이 null이거나 음수이면 예외가 발생한다.")
        @ParameterizedTest
        @MethodSource("createProductInvalidPrice")
        void createProductInvalidPriceTest(Product product) {
            // when & then
            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        static Stream<Arguments> createProductInvalidPrice() {
            return Stream.of(
                    Arguments.arguments(ProductFixture.newOne(-1000)),
                    Arguments.arguments(ProductFixture.newOne((BigDecimal) null))
            );
        }

        @DisplayName("[예외] 상품의 이름은 null일 수 없다.")
        @Test
        void createProductWithNameNullTest() {
            // given
            var product = ProductFixture.newOne((String) null);

            // when & then
            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[예외] 상품의 이름은 비속어를 포함할 수 없다.")
        @Test
        void createProductWithProfanityTest() {
            // given
            var product = ProductFixture.newOne();
            given(purgomalumClient.containsProfanity(any())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
