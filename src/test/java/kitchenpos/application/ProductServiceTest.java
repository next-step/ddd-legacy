package kitchenpos.application;

import kitchenpos.application.testfixture.MenuFixture;
import kitchenpos.application.testfixture.ProductFixture;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.domain.testfixture.MenuFakeRepository;
import kitchenpos.domain.testfixture.ProductFakeRepository;
import kitchenpos.domain.testfixture.PurgomalumFakeClient;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

    private ProductRepository productRepository;
    private MenuRepository menuRepository;
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void setUp() {
        productRepository = new ProductFakeRepository();
        menuRepository = new MenuFakeRepository();
        purgomalumClient = new PurgomalumFakeClient();
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @Nested
    @DisplayName("상품의 가격을 변경시,")
    class ChangeProductPrice {

        @DisplayName("가격이 정상 변동된다.")
        @Test
        void changedPriceTest() {
            // given
            var product = ProductFixture.newOne(5000);
            var savedProduct = productService.create(product);

            // when
            var updatedProduct = ProductFixture.newOne(5001);
            var actual = productService.changePrice(savedProduct.getId(), updatedProduct);

            // then
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(5001));
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

            // when & then
            assertThatThrownBy(() -> productService.changePrice(id, updatedProduct))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("[예외] 상품이 속한 메뉴의 가격이 메뉴 원가를 넘을 경우, 메뉴 가격은 변경되고, 메뉴는 비노출 처리된다.")
        @Test
        void productPriceExceptionTest() {
            // given
            var 닭고기 = ProductFixture.newOne("닭고기 300g", 5000);
            var product_닭고기 = productService.create(닭고기);
            var product_콜라 = ProductFixture.newOne(UUID.randomUUID(), "콜라", 500);
            var menu = MenuFixture.newOne(5500, List.of(product_닭고기, product_콜라));
            var updatedProduct = ProductFixture.newOne(4999);
            var savedMenu = menuRepository.save(menu);

            // when
            var actualProduct = productService.changePrice(product_닭고기.getId(), updatedProduct);

            // then
            var actualMenu = menuRepository.findById(savedMenu.getId());
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(actualProduct.getPrice()).isEqualTo(BigDecimal.valueOf(4999));
                softly.assertThat(actualMenu.get().isDisplayed()).isFalse();
            });
        }
    }

    @DisplayName("상품 목록 전체를 조회한다.")
    @Test
    void findAllTest() {
        // given
        var product_닭고기 = ProductFixture.newOne("닭고기 300g", 5000);
        var product_콜라 = ProductFixture.newOne("콜라", 500);
        List.of(product_닭고기, product_콜라).forEach(x-> productService.create(x));

        // when
        var actual = productService.findAll();

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actual.get(0).getName()).isEqualTo("닭고기 300g");
            softly.assertThat(actual.get(1).getName()).isEqualTo("콜라");
        });
    }

    @Nested
    @DisplayName("상품 생성시,")
    class CreateProduct {

        @DisplayName("상품이 정상 생성된다.")
        @Test
        void createTest() {
            // given
            var product = ProductFixture.newOne(UUID.randomUUID());

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
            var product = ProductFixture.newOne("비속어를 포함한 상품 제목");

            // when & then
            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
