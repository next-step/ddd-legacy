package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Product 는")
class ProductServiceTest {

    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final MenuRepository menuRepository = mock(MenuRepository.class);
    private final PurgomalumClient purgomalumClient = mock(PurgomalumClient.class);
    private final ProductService productService = new ProductService(
        productRepository,
        menuRepository,
        purgomalumClient
    );

    private static Product createProduct(BigDecimal price) {
        return createProduct("좋은말", price);
    }

    private static Product createProduct(String name, BigDecimal price) {
        return createProduct(UUID.randomUUID(), name, price);
    }

    private static Product createProduct(UUID productId, BigDecimal price) {
        return createProduct(productId, "좋은말", price);
    }

    private static Product 일원짜리_Product(UUID productId) {
        return createProduct(productId, "좋은말", BigDecimal.ONE);
    }

    private static Product createProduct(UUID productId, String name, BigDecimal price) {
        final Product product = new Product();
        product.setId(productId);
        product.setPrice(price);
        product.setName(name);
        return product;
    }

    private static Product 십원_상품(UUID productId) {
        return createProduct(productId, "십원짜리", BigDecimal.TEN);
    }

    private static MenuProduct createMenuProduct(Product product) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(2);
        menuProduct.setProduct(product);
        return menuProduct;
    }

    private static Menu createMenu(BigDecimal price, boolean display, MenuProduct... menuProducts) {
        final Menu menu = new Menu();
        menu.setPrice(price);
        menu.setDisplayed(display);
        menu.setMenuProducts(
            Arrays.asList(
                menuProducts
            )
        );
        return menu;
    }

    @DisplayName("등록할 수 있다.")
    @Nested
    class 등록할_수_있다 {

        @Nested
        @DisplayName("가격은")
        class 가격은 {

            @ParameterizedTest(name = "{0} 인 경우")
            @DisplayName("비어있거나 0보다 작으면 등록 불가능하다.")
            @NullSource
            @CsvSource("-1")
            void 비어있거나_0보다_작으면_등록_불가능하다(BigDecimal price) {
                final Product product = createProduct(price);

                assertThatIllegalArgumentException()
                    .isThrownBy(() -> productService.create(product));
            }

            @ParameterizedTest(name = "{0} 인 경우")
            @DisplayName("0 이상이면 등록 가능하다.")
            @CsvSource({"0", "1"})
            void 영_이상이면_등록_가능하다(BigDecimal price) {
                // given
                final String productName = "좋은말";
                final Product product = createProduct(productName, price);

                doReturn(false).when(purgomalumClient).containsProfanity(productName);
                doReturn(product).when(productRepository).save(any());

                // when
                final Product actual = productService.create(product);
                // then
                assertAll(
                    () -> assertThat(actual.getPrice()).isEqualTo(price),
                    () -> assertThat(actual.getName()).isEqualTo(productName)
                );
            }
        }

        @Nested
        @DisplayName("이름은")
        class 이름은 {

            private final String 나쁜말 = "나쁜말";
            private final BigDecimal 일원 = BigDecimal.ONE;

            @ParameterizedTest(name = "{0} 인 경우")
            @DisplayName("비어있거나 비속어인 경우 등록 불가능하다.")
            @NullSource
            @ValueSource(strings = "나쁜말")
            void 비어있거나_비속어인_경우_등록_불가능하다(String name) {
                final Product product = createProduct(name, 일원);

                doReturn(true).when(purgomalumClient).containsProfanity(나쁜말);

                assertThatIllegalArgumentException()
                    .isThrownBy(() -> productService.create(product));
            }

            @Test
            @DisplayName("비어 있지 않고 비속어가 아닌 경우 등록 가능하다.")
            void 비어_있지_않고_비속어가_아닌_경우_등록_가능하다() {
                // given
                final String 좋은말 = "좋은말";
                final Product product = createProduct(좋은말, 일원);

                doReturn(false).when(purgomalumClient).containsProfanity(좋은말);
                doReturn(product).when(productRepository).save(any());

                // when
                final Product actual = productService.create(product);

                // then
                assertThat(actual.getName()).isEqualTo(좋은말);
            }
        }
    }

    @Nested
    @DisplayName("가격이")
    class 가격이 {

        @Test
        @DisplayName("Product 가 존재하지 않는 경우 변경이 불가능하다.")
        void Product_가_존재하지_않는_경우_변경이_불가능하다() {
            // given
            final UUID productId = UUID.randomUUID();
            final Product product = 일원짜리_Product(productId);

            doReturn(Optional.empty()).when(productRepository).findById(productId);
            // when // then
            assertThatThrownBy(() -> productService.changePrice(productId, product))
                .isInstanceOf(NoSuchElementException.class);
        }

        @ParameterizedTest(name = "{0} 인 경우")
        @DisplayName("비어있거나 0보다 작다면 변경 불가능하다.")
        @NullSource
        @CsvSource("-1")
        void 비어있거나_0보다_작다면_변경_불가능하다(BigDecimal price) {
            // given
            final UUID productId = UUID.randomUUID();
            final Product product = createProduct(productId, price);

            // when // then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> productService.changePrice(productId, product));
        }

        @ParameterizedTest(name = "{0} 인 경우")
        @DisplayName("가격이 0 이상이면 변경 가능하다")
        @CsvSource({"0", "1"})
        void 가격이_0_이상이면_변경_가능하다(BigDecimal price) {
            // given
            final UUID productId = UUID.randomUUID();
            final Product product = createProduct(productId, price);

            doReturn(Optional.of(product)).when(productRepository).findById(productId);
            // when
            final Product actual = productService.changePrice(productId, product);
            // then
            assertThat(actual.getPrice()).isEqualTo(price);
        }



        @DisplayName("메뉴들의 가격이 각 메뉴 상품의 가격의 합보다")
        @Nested
        class 메뉴들의_가격이_각_메뉴_상품의_가격의_합보다 {

            @Test
            @DisplayName("큰 경우 메뉴는 전시되지 않는다.")
            void 큰_경우_메뉴는_전시되지_않는다() {
                // given

                final UUID productId = UUID.randomUUID();
                final Product product = 십원_상품(productId);
                final MenuProduct menuProduct = createMenuProduct(product);
                final Menu menu = createMenu(BigDecimal.valueOf(100), true, menuProduct);

                doReturn(Optional.of(product)).when(productRepository).findById(productId);
                doReturn(Arrays.asList(menu)).when(menuRepository).findAllByProductId(productId);

                // when
                productService.changePrice(productId, product);

                // then
                assertThat(menu.isDisplayed()).isEqualTo(false);
            }

            @ParameterizedTest(name = "{0} 인 경우")
            @DisplayName("작은 경우 메뉴의 전시 상태는 그대로 이다.")
            @ValueSource(booleans = {true, false})
            void 작은_경우_메뉴의_전시_상태는_그대로_이다(boolean displayed) {
                // given
                final UUID productId = UUID.randomUUID();
                final Product product = 십원_상품(productId);

                final MenuProduct menuProduct = createMenuProduct(product);

                final Menu menu = createMenu(
                    BigDecimal.ONE,
                    displayed,
                    menuProduct
                );

                doReturn(Optional.of(product)).when(productRepository).findById(productId);
                doReturn(Arrays.asList(menu)).when(menuRepository).findAllByProductId(productId);

                // when
                productService.changePrice(productId, product);

                // then
                assertThat(menu.isDisplayed()).isEqualTo(displayed);
            }
        }
    }
}
