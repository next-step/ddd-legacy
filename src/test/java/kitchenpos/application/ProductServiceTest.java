package kitchenpos.application;

import kitchenpos.application.testFixture.MenuFixture;
import kitchenpos.application.testFixture.ProductFixture;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.SoftAssertions;
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
import java.util.NoSuchElementException;
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
            var product_닭고기 = ProductFixture.newOne(id, "닭고기 300g", 5000);
            var product_콜라 = ProductFixture.newOne(id, "콜라", 500);
            var menu = MenuFixture.newOne(5500, List.of(product_닭고기, product_콜라));
            var updatedProduct = ProductFixture.newOne(5001);
            given(productRepository.findById(any())).willReturn(Optional.of(product_닭고기));
            given(menuRepository.findAllByProductId(any())).willReturn(List.of(menu));

            // when
            var actual = productService.changePrice(id, updatedProduct);

            // then
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(5001));
                softly.assertThat(menu.isDisplayed()).isTrue();
            });
        }

        @DisplayName("[예와] 변경할 상품의 가격은 null이거나 음수일 경우 예외가 발생한다;.")
        @ParameterizedTest
        @MethodSource("changeProductPriceInvalidPrice")
        void changePriceInvalidPriceTest(Product updatedProduct) {
            // when & then
            assertThatThrownBy(() -> productService.changePrice(UUID.randomUUID(), updatedProduct))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        static Stream<Arguments> changeProductPriceInvalidPrice() {
            return Stream.of(
                    Arguments.arguments(ProductFixture.newOne(-1000)),
                    Arguments.arguments(ProductFixture.newOne((BigDecimal) null))
            );
        }

        @DisplayName("[예외] 상품이 존재하지 않을 경우 예외가 발생한다.")
        @Test
        void notFoundProductExceptionTest() {
            // given
            var id = UUID.randomUUID();
            var updatedProduct = ProductFixture.newOne(4999);
            given(productRepository.findById(any())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productService.changePrice(id, updatedProduct))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("[예외] 상품이 속한 메뉴의 가격이 메뉴 원가를 넘을 경우, 메뉴 가격은 변경되고, 메뉴는 비노출 처리된다.")
        @Test
        void productPriceExceptionTest() {
            // given
            var product_닭고기_id = UUID.randomUUID();
            var product_닭고기 = ProductFixture.newOne(product_닭고기_id, "닭고기 300g", 5000);
            var product_콜라_id = UUID.randomUUID();
            var product_콜라 = ProductFixture.newOne(product_콜라_id, "콜라", 500);
            var menu = MenuFixture.newOne(5500, List.of(product_닭고기, product_콜라));
            var updatedProduct = ProductFixture.newOne(4999);
            given(productRepository.findById(any())).willReturn(Optional.of(product_닭고기));
            given(menuRepository.findAllByProductId(any())).willReturn(List.of(menu));

            // when
            var actual = productService.changePrice(product_닭고기_id, updatedProduct);

            // then
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(4999));
                softly.assertThat(menu.isDisplayed()).isFalse();
            });
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
